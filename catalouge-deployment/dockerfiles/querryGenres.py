from pyspark.sql import SparkSession
from pyspark.sql.functions import explode, col
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, LongType, ArrayType, MapType
from utils import FS, SPARK_ENV, get_spark_context

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

def count_distinct_genre_keys(df):
    genre_keys_df = df.select(explode(col("genre_counts")).alias("genre", "count"))
    distinct_genres_df = genre_keys_df.select("genre").distinct()
    
    distinct_genres_list = distinct_genres_df.rdd.flatMap(lambda x: x).collect()
    genres_string = ", ".join(distinct_genres_list)
    
    print(genres_string)

if __name__ == __main__:
    spark= get_spark_context(app_name="Catalouge", config=SPARK_ENV.K8S)
    df = spark.read.schema(schema).json(FS)

    count_distinct_genre_keys(df)
