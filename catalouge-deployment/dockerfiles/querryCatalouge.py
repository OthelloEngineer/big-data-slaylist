from pyspark.sql import SparkSession, functions as F
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, LongType, ArrayType, MapType
from pyspark.sql.functions import desc, asc, col, udf, lit
from utils import FS, SPARK_ENV, get_spark_context
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

THRESHHOLD = 0.01


def filter_playlists_by_genres(df, genre1, genre2):

    df_with_genre_check = df.withColumn(
        "genre1_count", F.when(F.col("genre_counts").getItem(genre1).isNotNull(), F.col("genre_counts").getItem(genre1)).otherwise(0)
    ).withColumn(
        "genre2_count", F.when(F.col("genre_counts").getItem(genre2).isNotNull(), F.col("genre_counts").getItem(genre2)).otherwise(0)
    )

   
    df_with_percentage = df_with_genre_check.withColumn(
        "genre1_percentage", F.col("genre1_count") / F.col("num_tracks")
    ).withColumn(
        "genre2_percentage", F.col("genre2_count") / F.col("num_tracks")
    )

    filtered_df = df_with_percentage.filter(
        (F.col("genre1_percentage") >= THRESHHOLD) &
        (F.col("genre2_percentage") >= THRESHHOLD)
    )

    final_df = filtered_df.drop("genre1_count", "genre2_count", "genre1_percentage", "genre2_percentage")

    return final_df

def get_catalouge(genre1, genre2):
    spark= get_spark_context(app_name="Catalouge", config=SPARK_ENV.K8S)

    df = spark.read.schema(schema).json(FS)
    filteret_df = filter_playlists_by_genres(df,genre1,genre2)
    sorteddf = filteret_df.sort(desc("num_followers"))
    top_playlists = sorteddf.take(100)
    
    dict_response = [row.asDict() for row in top_playlists]

    spark.stop()

    return dict_response

    

   
    
if __name__ == "__main__":    
    get_catalouge("pop", "indie pop")

