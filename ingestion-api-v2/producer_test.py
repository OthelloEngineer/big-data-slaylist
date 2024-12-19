from kafka import KafkaProducer, KafkaConsumer

producer = KafkaProducer(bootstrap_servers='localhost:9093')
producer.send('test_topic', b'test_message')
producer.flush()
print("Message sent successfully!")