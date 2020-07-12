package org.olivet.books;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.squareup.picasso.Picasso;

public class Book implements Parcelable {
    public String id;
    public String title;
    public String subTitle;
    public String authors;
    public String publishers;
    public String publishedDate;
    public String description;
    public String thumbnail;

    public Book(String id, String title, String subTitle, String[] authors, String publishers, String publishedDate, String description, String thumbnail) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.authors = TextUtils.join(". ", authors) ;
        this.publishers = publishers;
        this.publishedDate = publishedDate;
        this.description=description;
        this.thumbnail=thumbnail;
    }

    protected Book(Parcel in) {
        id = in.readString();
        title = in.readString();
        subTitle = in.readString();
        authors = in.readString();
        publishers = in.readString();
        publishedDate = in.readString();
        description = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(subTitle);
        parcel.writeString(authors);
        parcel.writeString(publishers);
        parcel.writeString(publishedDate);
        parcel.writeString(description);
        parcel.writeString(thumbnail);
    }

    @BindingAdapter({"android:imageUrl"})
    public static void loadImage(ImageView view, String imageUrl){
        if(!imageUrl.isEmpty()){
            Picasso.with(view.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.book_open)
                    .into(view);
        }
        else{
            view.setBackgroundResource(R.drawable.book_open);
        }

    }
}
