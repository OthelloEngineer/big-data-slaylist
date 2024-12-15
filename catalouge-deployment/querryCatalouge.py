from pyspark.sql import SparkSession
from utils import FS, SPARK_ENV, get_spark_context

if __name__ == "__main__":

    spark = get_spark_context(app_name="Catalouge", config=SPARK_ENV.LOCAL)
    
    df = spark.read.json(FS)

    df.show()

    spark.stop()