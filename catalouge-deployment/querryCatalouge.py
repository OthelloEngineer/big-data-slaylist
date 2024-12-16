import flameprof
from pyspark.sql import SparkSession
from utils import FS, SPARK_ENV, get_spark_context

def get_catalouge(genre1, genre2):
    schema = "name STRING, address STRING, email STRING, phone_number STRING, company STRING, job STRING, city STRING, timestamp STRING"
    spark = get_spark_context(app_name="Catalouge", config=SPARK_ENV.K8S)
    
    df = spark.read.schema(schema).json(FS)
    df.cache()
    df.limit(10).show()
    print(genre1 + " " + genre2)
    spark.stop()

if __name__ == "__main__":
    get_catalouge("hello1", "hello2")
