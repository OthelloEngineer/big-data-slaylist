from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, LongType, ArrayType, MapType
from pyspark.sql.functions import col, lit, rand, explode, array, concat_ws
from pyspark.ml.feature import StringIndexer, VectorAssembler
from pyspark.ml.regression import LinearRegression, LinearRegressionTrainingSummary
from pyspark.ml import Pipeline, PipelineModel
from utils import FS, SPARK_ENV, get_spark_context
import random
import string
import json

schema = StructType([
    StructField("name", StringType(), True),
    StructField("collaborative", StringType(), True),
    StructField("pid", IntegerType(), True),
    StructField("modified_at", LongType(), True),
    StructField("num_tracks", IntegerType(), True),
    StructField("num_albums", IntegerType(), True),
    StructField("num_followers", IntegerType(), True),
    StructField("tracks", ArrayType(
        StructType([
            StructField("pos", IntegerType(), True),
            StructField("artist_name", StringType(), True),
            StructField("track_uri", StringType(), True),
            StructField("artist_uri", StringType(), True),
            StructField("track_name", StringType(), True),
            StructField("album_uri", StringType(), True),
            StructField("duration_ms", IntegerType(), True),
            StructField("album_name", StringType(), True),
            StructField("artist_data", StructType([
                StructField("genres", ArrayType(StringType()), True),
                StructField("name", StringType(), True),
                StructField("popularity", IntegerType(), True),
                StructField("uri", StringType(), True)
            ]))
        ])
    ), True),
    StructField("num_edits", IntegerType(), True),
    StructField("duration_ms", LongType(), True),
    StructField("num_artists", IntegerType(), True),
    StructField("origin", StringType(), True),
    StructField("genre_counts", MapType(StringType(), IntegerType()), True),
])


def data_preprocessing(df):
    
    # Fill null values in the DataFrame with appropriate defaults
    df = df.fillna({
        "name": "unknown",                 # Default for playlist name
        "collaborative": "false",         # Default for collaborative flag
        "pid": 0,                         # Default for playlist ID
        "modified_at": 0,                 # Default for modification timestamp
        "num_tracks": 0,                  # Default for number of tracks
        "num_albums": 0,                  # Default for number of albums
        "num_followers": 0,               # Default for followers count
        "tracks": [],                     # Default for track list
        "num_edits": 0,                   # Default for number of edits
        "duration_ms": 0,                 # Default for total duration
        "num_artists": 0,                 # Default for number of artists
        "origin": "unknown",              # Default for origin
        "genre_counts": {},               # Default for genre counts
    })

    # Fill nested null values within the `tracks` field
    df = df.withColumn(
        "tracks",
        explode(
            col("tracks")
        ).alias("track")
    ).fillna({
        "track.pos": 0,
        "track.artist_name": "unknown",
        "track.track_uri": "unknown",
        "track.artist_uri": "unknown",
        "track.track_name": "unknown",
        "track.album_uri": "unknown",
        "track.duration_ms": 0,
        "track.album_name": "unknown",
        "track.artist_data.genres": [],
        "track.artist_data.name": "unknown",
        "track.artist_data.popularity": 0,
        "track.artist_data.uri": "unknown",
    })

    # creates a flat structure for the data
    exploded_tracks = df.select(
        explode(col("tracks")).alias("track"),
        col("num_followers")
    ).select(
        col("track.artist_name"),
        col("track.track_name"),
        col("track.artist_data.genres").alias("genres"),
        col("track.artist_data.popularity").alias("popularity"),
        col("num_followers"),
    )

    # converts genres array into a string
    exploded_tracks = exploded_tracks.withColumn("genres_str", concat_ws(",", col("genres")))

    # index the genres
    indexer = StringIndexer(inputCol="genres_str", outputCol="genre_index")
    indexed_tracks = indexer.fit(exploded_tracks).transform(exploded_tracks)

    # creates a feature vector of the inputs
    assembler = VectorAssembler(
        inputCols=["genre_index", "popularity", "num_followers"],
        outputCol="features"
    )

    preprocessed_data = assembler.transform(indexed_tracks).select("features", "popularity")

    return preprocessed_data


def train_test_split(preprocessed_data):
    train_data, test_data = preprocessed_data.randomSplit([0.8, 0.2], seed=1234)

    return train_data, test_data


def train_model():
    spark = get_spark_context(app_name="MLModel", config=SPARK_ENV.K8S)
    
    print("Starting the Spark session...")
    df = spark.read.schema(schema).json(FS)
    
    print("Performing data preprocessing...")
    preprocessed_data = data_preprocessing(df)
    train_data, test_data = train_test_split(preprocessed_data)
    print("Data preprocessing completed.")
    
    print("Training the Linear Regression model...")
    lr = LinearRegression(featuresCol="features", labelCol="popularity", regParam=0.1)
    lr_model = lr.fit(train_data)

    # Logging model information
    print("\nModel training completed.")
    print(f"Model Coefficients: {lr_model.coefficients}")
    print(f"Model Intercept: {lr_model.intercept}")
    
    if hasattr(lr_model, 'summary'):
        summary: LinearRegressionTrainingSummary = lr_model.summary
        print("\nTraining Summary:")
        print(f"Number of Iterations: {summary.totalIterations}")
        print(f"Root Mean Squared Error (RMSE): {summary.rootMeanSquaredError}")
        print(f"R2 Score: {summary.r2}")
        print(f"Explained Variance: {summary.explainedVariance}")
        print("Residuals Sample:")
        summary.residuals.show(5)

    model_save_path = "hdfs://namenode:9000/models/"
    print(f"Saving the model to {model_save_path}...")
    lr_model.save(model_save_path)
    print("Model saved successfully.")

    print("Generating predictions...")
    predictions = lr_model.transform(test_data)
    predictions.select("features", "popularity", "prediction").show(10, truncate=False)
    print("Predictions displayed.")

    print("Stopping the Spark session.")
    spark.stop()


if __name__ == "__main__":    
    train_model()