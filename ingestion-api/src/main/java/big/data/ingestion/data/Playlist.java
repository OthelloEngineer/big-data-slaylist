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
public class Playlist extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -1120741419772855476L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Playlist\",\"namespace\":\"big.data.ingestion.data\",\"fields\":[{\"name\":\"songs\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Song\",\"fields\":[{\"name\":\"track_uri\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"track_name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"artist\",\"type\":[\"null\",{\"type\":\"record\",\"name\":\"Artist\",\"fields\":[{\"name\":\"artist_name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"artist_uri\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"genres\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}},{\"name\":\"popularity\",\"type\":\"int\"}]}],\"default\":null},{\"name\":\"duration_ms\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}}},{\"name\":\"playlist_name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"followers\",\"type\":\"int\"},{\"name\":\"num_artists\",\"type\":\"int\"},{\"name\":\"num_songs\",\"type\":\"int\"},{\"name\":\"num_albums\",\"type\":\"int\"},{\"name\":\"genres\",\"type\":{\"type\":\"map\",\"values\":\"int\",\"avro.java.string\":\"String\"}},{\"name\":\"origin\",\"type\":{\"type\":\"enum\",\"name\":\"Origin\",\"symbols\":[\"USER\",\"DATASET\",\"SPOTIFY_TOP\"]}},{\"name\":\"pid\",\"type\":\"int\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<Playlist> ENCODER =
      new BinaryMessageEncoder<>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<Playlist> DECODER =
      new BinaryMessageDecoder<>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<Playlist> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<Playlist> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<Playlist> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this Playlist to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a Playlist from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a Playlist instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static Playlist fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.util.List<big.data.ingestion.data.Song> songs;
  private java.lang.String playlist_name;
  private int followers;
  private int num_artists;
  private int num_songs;
  private int num_albums;
  private java.util.Map<java.lang.String,java.lang.Integer> genres;
  private big.data.ingestion.data.Origin origin;
  private int pid;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public Playlist() {}

  /**
   * All-args constructor.
   * @param songs The new value for songs
   * @param playlist_name The new value for playlist_name
   * @param followers The new value for followers
   * @param num_artists The new value for num_artists
   * @param num_songs The new value for num_songs
   * @param num_albums The new value for num_albums
   * @param genres The new value for genres
   * @param origin The new value for origin
   * @param pid The new value for pid
   */
  public Playlist(java.util.List<big.data.ingestion.data.Song> songs, java.lang.String playlist_name, java.lang.Integer followers, java.lang.Integer num_artists, java.lang.Integer num_songs, java.lang.Integer num_albums, java.util.Map<java.lang.String,java.lang.Integer> genres, big.data.ingestion.data.Origin origin, java.lang.Integer pid) {
    this.songs = songs;
    this.playlist_name = playlist_name;
    this.followers = followers;
    this.num_artists = num_artists;
    this.num_songs = num_songs;
    this.num_albums = num_albums;
    this.genres = genres;
    this.origin = origin;
    this.pid = pid;
  }

  @Override
  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }

  @Override
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }

  // Used by DatumWriter.  Applications should not call.
  @Override
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return songs;
    case 1: return playlist_name;
    case 2: return followers;
    case 3: return num_artists;
    case 4: return num_songs;
    case 5: return num_albums;
    case 6: return genres;
    case 7: return origin;
    case 8: return pid;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @Override
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: songs = (java.util.List<big.data.ingestion.data.Song>)value$; break;
    case 1: playlist_name = value$ != null ? value$.toString() : null; break;
    case 2: followers = (java.lang.Integer)value$; break;
    case 3: num_artists = (java.lang.Integer)value$; break;
    case 4: num_songs = (java.lang.Integer)value$; break;
    case 5: num_albums = (java.lang.Integer)value$; break;
    case 6: genres = (java.util.Map<java.lang.String,java.lang.Integer>)value$; break;
    case 7: origin = (big.data.ingestion.data.Origin)value$; break;
    case 8: pid = (java.lang.Integer)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'songs' field.
   * @return The value of the 'songs' field.
   */
  public java.util.List<big.data.ingestion.data.Song> getSongs() {
    return songs;
  }


  /**
   * Sets the value of the 'songs' field.
   * @param value the value to set.
   */
  public void setSongs(java.util.List<big.data.ingestion.data.Song> value) {
    this.songs = value;
  }

  /**
   * Gets the value of the 'playlist_name' field.
   * @return The value of the 'playlist_name' field.
   */
  public java.lang.String getPlaylistName() {
    return playlist_name;
  }


  /**
   * Sets the value of the 'playlist_name' field.
   * @param value the value to set.
   */
  public void setPlaylistName(java.lang.String value) {
    this.playlist_name = value;
  }

  /**
   * Gets the value of the 'followers' field.
   * @return The value of the 'followers' field.
   */
  public int getFollowers() {
    return followers;
  }


  /**
   * Sets the value of the 'followers' field.
   * @param value the value to set.
   */
  public void setFollowers(int value) {
    this.followers = value;
  }

  /**
   * Gets the value of the 'num_artists' field.
   * @return The value of the 'num_artists' field.
   */
  public int getNumArtists() {
    return num_artists;
  }


  /**
   * Sets the value of the 'num_artists' field.
   * @param value the value to set.
   */
  public void setNumArtists(int value) {
    this.num_artists = value;
  }

  /**
   * Gets the value of the 'num_songs' field.
   * @return The value of the 'num_songs' field.
   */
  public int getNumSongs() {
    return num_songs;
  }


  /**
   * Sets the value of the 'num_songs' field.
   * @param value the value to set.
   */
  public void setNumSongs(int value) {
    this.num_songs = value;
  }

  /**
   * Gets the value of the 'num_albums' field.
   * @return The value of the 'num_albums' field.
   */
  public int getNumAlbums() {
    return num_albums;
  }


  /**
   * Sets the value of the 'num_albums' field.
   * @param value the value to set.
   */
  public void setNumAlbums(int value) {
    this.num_albums = value;
  }

  /**
   * Gets the value of the 'genres' field.
   * @return The value of the 'genres' field.
   */
  public java.util.Map<java.lang.String,java.lang.Integer> getGenres() {
    return genres;
  }


  /**
   * Sets the value of the 'genres' field.
   * @param value the value to set.
   */
  public void setGenres(java.util.Map<java.lang.String,java.lang.Integer> value) {
    this.genres = value;
  }

  /**
   * Gets the value of the 'origin' field.
   * @return The value of the 'origin' field.
   */
  public big.data.ingestion.data.Origin getOrigin() {
    return origin;
  }


  /**
   * Sets the value of the 'origin' field.
   * @param value the value to set.
   */
  public void setOrigin(big.data.ingestion.data.Origin value) {
    this.origin = value;
  }

  /**
   * Gets the value of the 'pid' field.
   * @return The value of the 'pid' field.
   */
  public int getPid() {
    return pid;
  }


  /**
   * Sets the value of the 'pid' field.
   * @param value the value to set.
   */
  public void setPid(int value) {
    this.pid = value;
  }

  /**
   * Creates a new Playlist RecordBuilder.
   * @return A new Playlist RecordBuilder
   */
  public static big.data.ingestion.data.Playlist.Builder newBuilder() {
    return new big.data.ingestion.data.Playlist.Builder();
  }

  /**
   * Creates a new Playlist RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new Playlist RecordBuilder
   */
  public static big.data.ingestion.data.Playlist.Builder newBuilder(big.data.ingestion.data.Playlist.Builder other) {
    if (other == null) {
      return new big.data.ingestion.data.Playlist.Builder();
    } else {
      return new big.data.ingestion.data.Playlist.Builder(other);
    }
  }

  /**
   * Creates a new Playlist RecordBuilder by copying an existing Playlist instance.
   * @param other The existing instance to copy.
   * @return A new Playlist RecordBuilder
   */
  public static big.data.ingestion.data.Playlist.Builder newBuilder(big.data.ingestion.data.Playlist other) {
    if (other == null) {
      return new big.data.ingestion.data.Playlist.Builder();
    } else {
      return new big.data.ingestion.data.Playlist.Builder(other);
    }
  }

  /**
   * RecordBuilder for Playlist instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Playlist>
    implements org.apache.avro.data.RecordBuilder<Playlist> {

    private java.util.List<big.data.ingestion.data.Song> songs;
    private java.lang.String playlist_name;
    private int followers;
    private int num_artists;
    private int num_songs;
    private int num_albums;
    private java.util.Map<java.lang.String,java.lang.Integer> genres;
    private big.data.ingestion.data.Origin origin;
    private int pid;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(big.data.ingestion.data.Playlist.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.songs)) {
        this.songs = data().deepCopy(fields()[0].schema(), other.songs);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.playlist_name)) {
        this.playlist_name = data().deepCopy(fields()[1].schema(), other.playlist_name);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.followers)) {
        this.followers = data().deepCopy(fields()[2].schema(), other.followers);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.num_artists)) {
        this.num_artists = data().deepCopy(fields()[3].schema(), other.num_artists);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.num_songs)) {
        this.num_songs = data().deepCopy(fields()[4].schema(), other.num_songs);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.num_albums)) {
        this.num_albums = data().deepCopy(fields()[5].schema(), other.num_albums);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.genres)) {
        this.genres = data().deepCopy(fields()[6].schema(), other.genres);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (isValidValue(fields()[7], other.origin)) {
        this.origin = data().deepCopy(fields()[7].schema(), other.origin);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
      if (isValidValue(fields()[8], other.pid)) {
        this.pid = data().deepCopy(fields()[8].schema(), other.pid);
        fieldSetFlags()[8] = other.fieldSetFlags()[8];
      }
    }

    /**
     * Creates a Builder by copying an existing Playlist instance
     * @param other The existing instance to copy.
     */
    private Builder(big.data.ingestion.data.Playlist other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.songs)) {
        this.songs = data().deepCopy(fields()[0].schema(), other.songs);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.playlist_name)) {
        this.playlist_name = data().deepCopy(fields()[1].schema(), other.playlist_name);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.followers)) {
        this.followers = data().deepCopy(fields()[2].schema(), other.followers);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.num_artists)) {
        this.num_artists = data().deepCopy(fields()[3].schema(), other.num_artists);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.num_songs)) {
        this.num_songs = data().deepCopy(fields()[4].schema(), other.num_songs);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.num_albums)) {
        this.num_albums = data().deepCopy(fields()[5].schema(), other.num_albums);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.genres)) {
        this.genres = data().deepCopy(fields()[6].schema(), other.genres);
        fieldSetFlags()[6] = true;
      }
      if (isValidValue(fields()[7], other.origin)) {
        this.origin = data().deepCopy(fields()[7].schema(), other.origin);
        fieldSetFlags()[7] = true;
      }
      if (isValidValue(fields()[8], other.pid)) {
        this.pid = data().deepCopy(fields()[8].schema(), other.pid);
        fieldSetFlags()[8] = true;
      }
    }

    /**
      * Gets the value of the 'songs' field.
      * @return The value.
      */
    public java.util.List<big.data.ingestion.data.Song> getSongs() {
      return songs;
    }


    /**
      * Sets the value of the 'songs' field.
      * @param value The value of 'songs'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setSongs(java.util.List<big.data.ingestion.data.Song> value) {
      validate(fields()[0], value);
      this.songs = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'songs' field has been set.
      * @return True if the 'songs' field has been set, false otherwise.
      */
    public boolean hasSongs() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'songs' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearSongs() {
      songs = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'playlist_name' field.
      * @return The value.
      */
    public java.lang.String getPlaylistName() {
      return playlist_name;
    }


    /**
      * Sets the value of the 'playlist_name' field.
      * @param value The value of 'playlist_name'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setPlaylistName(java.lang.String value) {
      validate(fields()[1], value);
      this.playlist_name = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'playlist_name' field has been set.
      * @return True if the 'playlist_name' field has been set, false otherwise.
      */
    public boolean hasPlaylistName() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'playlist_name' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearPlaylistName() {
      playlist_name = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'followers' field.
      * @return The value.
      */
    public int getFollowers() {
      return followers;
    }


    /**
      * Sets the value of the 'followers' field.
      * @param value The value of 'followers'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setFollowers(int value) {
      validate(fields()[2], value);
      this.followers = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'followers' field has been set.
      * @return True if the 'followers' field has been set, false otherwise.
      */
    public boolean hasFollowers() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'followers' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearFollowers() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'num_artists' field.
      * @return The value.
      */
    public int getNumArtists() {
      return num_artists;
    }


    /**
      * Sets the value of the 'num_artists' field.
      * @param value The value of 'num_artists'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setNumArtists(int value) {
      validate(fields()[3], value);
      this.num_artists = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'num_artists' field has been set.
      * @return True if the 'num_artists' field has been set, false otherwise.
      */
    public boolean hasNumArtists() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'num_artists' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearNumArtists() {
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'num_songs' field.
      * @return The value.
      */
    public int getNumSongs() {
      return num_songs;
    }


    /**
      * Sets the value of the 'num_songs' field.
      * @param value The value of 'num_songs'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setNumSongs(int value) {
      validate(fields()[4], value);
      this.num_songs = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'num_songs' field has been set.
      * @return True if the 'num_songs' field has been set, false otherwise.
      */
    public boolean hasNumSongs() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'num_songs' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearNumSongs() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'num_albums' field.
      * @return The value.
      */
    public int getNumAlbums() {
      return num_albums;
    }


    /**
      * Sets the value of the 'num_albums' field.
      * @param value The value of 'num_albums'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setNumAlbums(int value) {
      validate(fields()[5], value);
      this.num_albums = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'num_albums' field has been set.
      * @return True if the 'num_albums' field has been set, false otherwise.
      */
    public boolean hasNumAlbums() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'num_albums' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearNumAlbums() {
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'genres' field.
      * @return The value.
      */
    public java.util.Map<java.lang.String,java.lang.Integer> getGenres() {
      return genres;
    }


    /**
      * Sets the value of the 'genres' field.
      * @param value The value of 'genres'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setGenres(java.util.Map<java.lang.String,java.lang.Integer> value) {
      validate(fields()[6], value);
      this.genres = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'genres' field has been set.
      * @return True if the 'genres' field has been set, false otherwise.
      */
    public boolean hasGenres() {
      return fieldSetFlags()[6];
    }


    /**
      * Clears the value of the 'genres' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearGenres() {
      genres = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'origin' field.
      * @return The value.
      */
    public big.data.ingestion.data.Origin getOrigin() {
      return origin;
    }


    /**
      * Sets the value of the 'origin' field.
      * @param value The value of 'origin'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setOrigin(big.data.ingestion.data.Origin value) {
      validate(fields()[7], value);
      this.origin = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'origin' field has been set.
      * @return True if the 'origin' field has been set, false otherwise.
      */
    public boolean hasOrigin() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'origin' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearOrigin() {
      origin = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    /**
      * Gets the value of the 'pid' field.
      * @return The value.
      */
    public int getPid() {
      return pid;
    }


    /**
      * Sets the value of the 'pid' field.
      * @param value The value of 'pid'.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder setPid(int value) {
      validate(fields()[8], value);
      this.pid = value;
      fieldSetFlags()[8] = true;
      return this;
    }

    /**
      * Checks whether the 'pid' field has been set.
      * @return True if the 'pid' field has been set, false otherwise.
      */
    public boolean hasPid() {
      return fieldSetFlags()[8];
    }


    /**
      * Clears the value of the 'pid' field.
      * @return This builder.
      */
    public big.data.ingestion.data.Playlist.Builder clearPid() {
      fieldSetFlags()[8] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Playlist build() {
      try {
        Playlist record = new Playlist();
        record.songs = fieldSetFlags()[0] ? this.songs : (java.util.List<big.data.ingestion.data.Song>) defaultValue(fields()[0]);
        record.playlist_name = fieldSetFlags()[1] ? this.playlist_name : (java.lang.String) defaultValue(fields()[1]);
        record.followers = fieldSetFlags()[2] ? this.followers : (java.lang.Integer) defaultValue(fields()[2]);
        record.num_artists = fieldSetFlags()[3] ? this.num_artists : (java.lang.Integer) defaultValue(fields()[3]);
        record.num_songs = fieldSetFlags()[4] ? this.num_songs : (java.lang.Integer) defaultValue(fields()[4]);
        record.num_albums = fieldSetFlags()[5] ? this.num_albums : (java.lang.Integer) defaultValue(fields()[5]);
        record.genres = fieldSetFlags()[6] ? this.genres : (java.util.Map<java.lang.String,java.lang.Integer>) defaultValue(fields()[6]);
        record.origin = fieldSetFlags()[7] ? this.origin : (big.data.ingestion.data.Origin) defaultValue(fields()[7]);
        record.pid = fieldSetFlags()[8] ? this.pid : (java.lang.Integer) defaultValue(fields()[8]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<Playlist>
    WRITER$ = (org.apache.avro.io.DatumWriter<Playlist>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<Playlist>
    READER$ = (org.apache.avro.io.DatumReader<Playlist>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    long size0 = this.songs.size();
    out.writeArrayStart();
    out.setItemCount(size0);
    long actualSize0 = 0;
    for (big.data.ingestion.data.Song e0: this.songs) {
      actualSize0++;
      out.startItem();
      e0.customEncode(out);
    }
    out.writeArrayEnd();
    if (actualSize0 != size0)
      throw new java.util.ConcurrentModificationException("Array-size written was " + size0 + ", but element count was " + actualSize0 + ".");

    out.writeString(this.playlist_name);

    out.writeInt(this.followers);

    out.writeInt(this.num_artists);

    out.writeInt(this.num_songs);

    out.writeInt(this.num_albums);

    long size1 = this.genres.size();
    out.writeMapStart();
    out.setItemCount(size1);
    long actualSize1 = 0;
    for (java.util.Map.Entry<java.lang.String, java.lang.Integer> e1: this.genres.entrySet()) {
      actualSize1++;
      out.startItem();
      out.writeString(e1.getKey());
      java.lang.Integer v1 = e1.getValue();
      out.writeInt(v1);
    }
    out.writeMapEnd();
    if (actualSize1 != size1)
      throw new java.util.ConcurrentModificationException("Map-size written was " + size1 + ", but element count was " + actualSize1 + ".");

    out.writeEnum(this.origin.ordinal());

    out.writeInt(this.pid);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      long size0 = in.readArrayStart();
      java.util.List<big.data.ingestion.data.Song> a0 = this.songs;
      if (a0 == null) {
        a0 = new SpecificData.Array<big.data.ingestion.data.Song>((int)size0, SCHEMA$.getField("songs").schema());
        this.songs = a0;
      } else a0.clear();
      SpecificData.Array<big.data.ingestion.data.Song> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<big.data.ingestion.data.Song>)a0 : null);
      for ( ; 0 < size0; size0 = in.arrayNext()) {
        for ( ; size0 != 0; size0--) {
          big.data.ingestion.data.Song e0 = (ga0 != null ? ga0.peek() : null);
          if (e0 == null) {
            e0 = new big.data.ingestion.data.Song();
          }
          e0.customDecode(in);
          a0.add(e0);
        }
      }

      this.playlist_name = in.readString();

      this.followers = in.readInt();

      this.num_artists = in.readInt();

      this.num_songs = in.readInt();

      this.num_albums = in.readInt();

      long size1 = in.readMapStart();
      java.util.Map<java.lang.String,java.lang.Integer> m1 = this.genres; // Need fresh name due to limitation of macro system
      if (m1 == null) {
        m1 = new java.util.HashMap<java.lang.String,java.lang.Integer>((int)size1);
        this.genres = m1;
      } else m1.clear();
      for ( ; 0 < size1; size1 = in.mapNext()) {
        for ( ; size1 != 0; size1--) {
          java.lang.String k1 = null;
          k1 = in.readString();
          java.lang.Integer v1 = null;
          v1 = in.readInt();
          m1.put(k1, v1);
        }
      }

      this.origin = big.data.ingestion.data.Origin.values()[in.readEnum()];

      this.pid = in.readInt();

    } else {
      for (int i = 0; i < 9; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          long size0 = in.readArrayStart();
          java.util.List<big.data.ingestion.data.Song> a0 = this.songs;
          if (a0 == null) {
            a0 = new SpecificData.Array<big.data.ingestion.data.Song>((int)size0, SCHEMA$.getField("songs").schema());
            this.songs = a0;
          } else a0.clear();
          SpecificData.Array<big.data.ingestion.data.Song> ga0 = (a0 instanceof SpecificData.Array ? (SpecificData.Array<big.data.ingestion.data.Song>)a0 : null);
          for ( ; 0 < size0; size0 = in.arrayNext()) {
            for ( ; size0 != 0; size0--) {
              big.data.ingestion.data.Song e0 = (ga0 != null ? ga0.peek() : null);
              if (e0 == null) {
                e0 = new big.data.ingestion.data.Song();
              }
              e0.customDecode(in);
              a0.add(e0);
            }
          }
          break;

        case 1:
          this.playlist_name = in.readString();
          break;

        case 2:
          this.followers = in.readInt();
          break;

        case 3:
          this.num_artists = in.readInt();
          break;

        case 4:
          this.num_songs = in.readInt();
          break;

        case 5:
          this.num_albums = in.readInt();
          break;

        case 6:
          long size1 = in.readMapStart();
          java.util.Map<java.lang.String,java.lang.Integer> m1 = this.genres; // Need fresh name due to limitation of macro system
          if (m1 == null) {
            m1 = new java.util.HashMap<java.lang.String,java.lang.Integer>((int)size1);
            this.genres = m1;
          } else m1.clear();
          for ( ; 0 < size1; size1 = in.mapNext()) {
            for ( ; size1 != 0; size1--) {
              java.lang.String k1 = null;
              k1 = in.readString();
              java.lang.Integer v1 = null;
              v1 = in.readInt();
              m1.put(k1, v1);
            }
          }
          break;

        case 7:
          this.origin = big.data.ingestion.data.Origin.values()[in.readEnum()];
          break;

        case 8:
          this.pid = in.readInt();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









