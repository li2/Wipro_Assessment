package me.li2.android.wipro_assessment.data.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by weiyi on 15/02/2018.
 * https://github.com/li2
 */

@Entity(tableName = "countryIntroTable", indices = {@Index(value = {"title"}, unique = true)})
public class CountryIntroEntry {

    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * title : Beavers
     * description : Beavers are second only to humans in their ability to manipulate and change their environment. They can measure up to 1.3 metres long. A group of beavers is called a colony
     * imageHref : http://upload.wikimedia.org/wikipedia/commons/thumb/6/6b/American_Beaver.jpg/220px-American_Beaver.jpg
     */

    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("imageHref")
    private String imageHref;

    public CountryIntroEntry(int id, String title, String description, String imageHref) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageHref = imageHref;
    }

    @Ignore
    public CountryIntroEntry(String title, String description, String imageHref) {
        this.title = title;
        this.description = description;
        this.imageHref = imageHref;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageHref() {
        return imageHref;
    }

    @Override
    public String toString() {
        return "title: " + this.title + ", description: " + this.description + ", imageHref: " + this.imageHref;
    }
}
