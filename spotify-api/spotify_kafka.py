import kafka

class SpotifyKafka:
    def __init__(self, bootstrap_servers: str, id_topic: str, artist_topic: str):
        self.bootstrap_servers = bootstrap_servers
        self.artist_topic = artist_topic
        self.producer = kafka.KafkaProducer(bootstrap_servers=bootstrap_servers)
        self.consumer = kafka.KafkaConsumer(id_topic, bootstrap_servers=bootstrap_servers, group_id="spotifyapi")

    def produce(self, message: bytes):
        self.producer.send(self.artist_topic, message)

    def get_one_message(self) -> bytes:
        msg = next(self.consumer)
        msg_bytes = msg.value
        return msg_bytes
        
