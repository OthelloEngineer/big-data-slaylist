/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package big.data.ingestion.data;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class Song extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 4988485243661006968L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Song\",\"namespace\":\"big.data.ingestion.data\",\"fields\":[{\"name\":\"track_uri\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"track_name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"artist\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Artist\",\"fields\":[{\"name\":\"artist_name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"artist_uri\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"genres\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"popularity\",\"type\":\"int\"}]}],\"default\":null},{\"name\":\"duration_ms\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Song> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Song> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Song> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Song> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Song> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Song to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Song from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Song instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Song fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.String track_uri;
  private java.lang.String track_name;
  private big.data.ingestion.data.Artist artist;
  private java.lang.String duration_ms;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Song() {}

  /**
   * All-args constructor.
   * @param track_uri The new value for track_uri
   * @param track_name The new value for track_name
   * @param artist The new value for artist
   * @param duration_ms The new value for duration_ms
   */
  public Song(java.lang.String track_uri, java.lang.String track_name, big.data.ingestion.data.Artist artist, java.lang.String duration_ms) {
    this.track_uri = track_uri;
    this.track_name = track_name;
    this.artist = artist;
    this.duration_ms = duration_ms;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return track_uri;
    case 1: return track_name;
    case 2: return artist;
    case 3: return duration_ms;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: track_uri = value$ != null ? value$.toString() : null; break;
    case 1: track_name = value$ != null ? value$.toString() : null; break;
    case 2: artist = (big.data.ingestion.data.Artist)value$; break;
    case 3: duration_ms = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'track_uri' field.
   * @return The value of the 'track_uri' field.
   */
  public java.lang.String getTrackUri() {
    return track_uri;
  }


  /**
   * Sets the value of the 'track_uri' field.
   * @param value the value to set.
   */
  public void setTrackUri(java.lang.String value) {
    this.track_uri = value;
  }

  /**
   * Gets the value of the 'track_name' field.
   * @return The value of the 'track_name' field.
   */
  public java.lang.String getTrackName() {
    return track_name;
  }


  /**
   * Sets the value of the 'track_name' field.
   * @param value the value to set.
   */
  public void setTrackName(java.lang.String value) {
    this.track_name = value;
  }

  /**
   * Gets the value of the 'artist' field.
   * @return The value of the 'artist' field.
   */
  public big.data.ingestion.data.Artist getArtist() {
    return artist;
  }


  /**
   * Sets the value of the 'artist' field.
   * @param value the value to set.
   */
  public void setArtist(big.data.ingestion.data.Artist value) {
    this.artist = value;
  }

  /**
   * Gets the value of the 'duration_ms' field.
   * @return The value of the 'duration_ms' field.
   */
  public java.lang.String getDurationMs() {
    return duration_ms;
  }


  /**
   * Sets the value of the 'duration_ms' field.
   * @param value the value to set.
   */
  public void setDurationMs(java.lang.String value) {
    this.duration_ms = value;
  }

  /**
   * Creates a new Song RecordBuilder.
   * @return A new Song RecordBuilder
   */
  public static big.data.ingestion.data.Song.Builder newBuilder() {
    return new big.data.ingestion.data.Song.Builder();
  }

  /**
   * Creates a new Song RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Song RecordBuilder
   */
  public static big.data.ingestion.data.Song.Builder newBuilder(big.data.ingestion.data.Song.Builder other) {
    if (other == null) {
      return new big.data.ingestion.data.Song.Builder();
    } else {
      return new big.data.ingestion.data.Song.Builder(other);
    }
  }

  /**
   * Creates a new Song RecordBuilder by copying an existing Song instance.
   * @param other The existing instance to copy.
   * @return A new Song RecordBuilder
   */
  public static big.data.ingestion.data.Song.Builder newBuilder(big.data.ingestion.data.Song other) {
    if (other == null) {
      return new big.data.ingestion.data.Song.Builder();
    } else {
      return new big.data.ingestion.data.Song.Builder(other);
    }
  }

  /**
   * RecordBuilder for Song instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Song>
    implements org.apache.avro.data.RecordBuilder<Song> {

    private java.lang.String track_uri;
    private java.lang.String track_name;
    private big.data.ingestion.data.Artist artist;
    private big.data.ingestion.data.Artist.Builder artistBuilder;
    private java.lang.String duration_ms;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(big.data.ingestion.data.Song.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.track_uri)) {
        this.track_uri = data().deepCopy(fields()[0].schema(), other.track_uri);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.track_name)) {
        this.track_name = data().deepCopy(fields()[1].schema(), other.track_name);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.artist)) {
        this.artist = data().deepCopy(fields()[2].schema(), other.artist);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (other.hasArtistBuilder()) {
        this.artistBuilder = big.data.ingestion.data.Artist.newBuilder(other.getArtistBuilder());
      }
      if (isValidValue(fields()[3], other.duration_ms)) {
        this.duration_ms = data().deepCopy(fields()[3].schema(), other.duration_ms);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
    }

    /**
     * Creates a Builder by copying an existing Song instance
     * @param other The existing instance to copy.
     */
    private Builder(big.data.ingestion.data.Song other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.track_uri)) {
        this.track_uri = data().deepCopy(fields()[0].schema(), other.track_uri);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.track_name)) {
        this.track_name = data().deepCopy(fields()[1].schema(), other.track_name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.artist)) {
        this.artist = data().deepCopy(fields()[2].schema(), other.artist);
        fieldSetFlags()[2] = true;
      }
      this.artistBuilder = null;
      if (isValidValue(fields()[3], other.duration_ms)) {
        this.duration_ms = data().deepCopy(fields()[3].schema(), other.duration_ms);
        fieldSetFlags()[3] = true;
      }
    }

    /**
      * Gets the value of the 'track_uri' field.
      * @return The value.
      */
    public java.lang.String getTrackUri() {
      return track_uri;
    }


    /**
      * Sets the value of the 'track_uri' field.
      * @param value The value of 'track_uri'.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder setTrackUri(java.lang.String value) {
      validate(fields()[0], value);
      this.track_uri = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'track_uri' field has been set.
      * @return True if the 'track_uri' field has been set, false otherwise.
      */
    public boolean hasTrackUri() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'track_uri' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder clearTrackUri() {
      track_uri = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'track_name' field.
      * @return The value.
      */
    public java.lang.String getTrackName() {
      return track_name;
    }


    /**
      * Sets the value of the 'track_name' field.
      * @param value The value of 'track_name'.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder setTrackName(java.lang.String value) {
      validate(fields()[1], value);
      this.track_name = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'track_name' field has been set.
      * @return True if the 'track_name' field has been set, false otherwise.
      */
    public boolean hasTrackName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'track_name' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder clearTrackName() {
      track_name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'artist' field.
      * @return The value.
      */
    public big.data.ingestion.data.Artist getArtist() {
      return artist;
    }


    /**
      * Sets the value of the 'artist' field.
      * @param value The value of 'artist'.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder setArtist(big.data.ingestion.data.Artist value) {
      validate(fields()[2], value);
      this.artistBuilder = null;
      this.artist = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'artist' field has been set.
      * @return True if the 'artist' field has been set, false otherwise.
      */
    public boolean hasArtist() {
      return fieldSetFlags()[2];
    }

    /**
     * Gets the Builder instance for the 'artist' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public big.data.ingestion.data.Artist.Builder getArtistBuilder() {
      if (artistBuilder == null) {
        if (hasArtist()) {
          setArtistBuilder(big.data.ingestion.data.Artist.newBuilder(artist));
        } else {
          setArtistBuilder(big.data.ingestion.data.Artist.newBuilder());
        }
      }
      return artistBuilder;
    }

    /**
     * Sets the Builder instance for the 'artist' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public big.data.ingestion.data.Song.Builder setArtistBuilder(big.data.ingestion.data.Artist.Builder value) {
      clearArtist();
      artistBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'artist' field has an active Builder instance
     * @return True if the 'artist' field has an active Builder instance
     */
    public boolean hasArtistBuilder() {
      return artistBuilder != null;
    }

    /**
      * Clears the value of the 'artist' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder clearArtist() {
      artist = null;
      artistBuilder = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'duration_ms' field.
      * @return The value.
      */
    public java.lang.String getDurationMs() {
      return duration_ms;
    }


    /**
      * Sets the value of the 'duration_ms' field.
      * @param value The value of 'duration_ms'.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder setDurationMs(java.lang.String value) {
      validate(fields()[3], value);
      this.duration_ms = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'duration_ms' field has been set.
      * @return True if the 'duration_ms' field has been set, false otherwise.
      */
    public boolean hasDurationMs() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'duration_ms' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Song.Builder clearDurationMs() {
      duration_ms = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Song build() {
      try {
        Song record = new Song();
        record.track_uri = fieldSetFlags()[0] ? this.track_uri : (java.lang.String) defaultValue(fields()[0]);
        record.track_name = fieldSetFlags()[1] ? this.track_name : (java.lang.String) defaultValue(fields()[1]);
        if (artistBuilder != null) {
          try {
            record.artist = this.artistBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("artist"));
            throw e;
          }
        } else {
          record.artist = fieldSetFlags()[2] ? this.artist : (big.data.ingestion.data.Artist) defaultValue(fields()[2]);
        }
        record.duration_ms = fieldSetFlags()[3] ? this.duration_ms : (java.lang.String) defaultValue(fields()[3]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Song>
    WRITER$ = (org.apache.avro.io.DatumWriter<Song>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Song>
    READER$ = (org.apache.avro.io.DatumReader<Song>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.track_uri);

    out.writeString(this.track_name);

    if (this.artist == null) {
      out.writeIndex(0);
      out.writeNull();
    } else {
      out.writeIndex(1);
      this.artist.customEncode(out);
    }

    out.writeString(this.duration_ms);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.track_uri = in.readString();

      this.track_name = in.readString();

      if (in.readIndex() != 1) {
        in.readNull();
        this.artist = null;
      } else {
        if (this.artist == null) {
          this.artist = new big.data.ingestion.data.Artist();
        }
        this.artist.customDecode(in);
      }

      this.duration_ms = in.readString();

    } else {
      for (int i = 0; i < 4; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.track_uri = in.readString();
          break;

        case 1:
          this.track_name = in.readString();
          break;

        case 2:
          if (in.readIndex() != 1) {
            in.readNull();
            this.artist = null;
          } else {
            if (this.artist == null) {
              this.artist = new big.data.ingestion.data.Artist();
            }
            this.artist.customDecode(in);
          }
          break;

        case 3:
          this.duration_ms = in.readString();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









