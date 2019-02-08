package app.mobilebrainz.mediabrainz.data.room.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "recommends")
public class Recommend {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "tag")
    private String tag;

    @ColumnInfo(name = "number")
    private int number;

    public Recommend(@NonNull String tag, int number) {
        this.tag = tag;
        this.number = number;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
