from pyspark.sql import SparkSession
from pyspark.sql.functions import desc, asc
from utils import FS, SPARK_ENV, get_spark_context
import json

def get_catalouge(genre1, genre2):
    schema = "name STRING, address STRING, email STRING, phone_number STRING, company STRING, job STRING, city STRING, timestamp STRING"
    spark = get_spark_context(app_name="Catalouge", config=SPARK_ENV.K8S)
    
    df = spark.read.schema(schema).json(FS)
    df.cache()
    result = df.sort(asc("name")).limit(10).toJSON().collect()
    parsed_data = [json.loads(item) for item in result]
    print(genre1 + " " + genre2)
    spark.stop()
    return parsed_data

if __name__ == "__main__":    
    get_catalouge("hello1", "hello2")

