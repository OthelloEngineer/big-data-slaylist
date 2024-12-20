from pyspark.sql import SparkSession
from pyspark.sql.functions import desc, asc, col, udf, lit
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, MapType, LongType, BooleanType
from utils import FS, SPARK_ENV, get_spark_context
from pyspark.sql import functions as F
import json

schema = StructType([
    StructField("name", StringType(), True),
    StructField("collaborative", StringType(), True),
    StructField("pid", StringType(), True),
    StructField("modified_at", LongType(), True),
    StructField("num_tracks", IntegerType(), True),
    StructField("num_albums", IntegerType(), True),
    StructField("num_followers", IntegerType(), True),
    StructField("tracks", StringType(), True),  
    StructField("num_edits", IntegerType(), True),
    StructField("duration_ms", IntegerType(), True),
    StructField("num_artists", IntegerType(), True),
    StructField("origin", StringType(), True),
    StructField("genres", MapType(StringType(), IntegerType()), True),
])
THRESHHOLD = 0.01

def filter_playlists_by_genres(df, genre1, genre2):

    df_with_genre_check = df.withColumn(
        "genre1_count", F.when(F.col("genres").getItem(genre1).isNotNull(), F.col("genres").getItem(genre1)).otherwise(0)
    ).withColumn(
        "genre2_count", F.when(F.col("genres").getItem(genre2).isNotNull(), F.col("genres").getItem(genre2)).otherwise(0)
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

    return dict_response

    sorteddf.select("name","num_followers","genres").show(truncate=False)
if __name__ == "__main__":    
    get_catalouge("jazz", "indie")

