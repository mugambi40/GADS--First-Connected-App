package org.olivet.books;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
//import android.widget.SearchView;
//import android.support.v7.widget.SearchView;
import androidx.appcompat.widget.SearchView;

import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

import static org.olivet.books.ApiUtil.buildUri;

public class BookListActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ProgressBar mLoadingProgress;
    private RecyclerView rvBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);
        rvBooks = (RecyclerView) findViewById(R.id.rv_books);
        mLoadingProgress = (ProgressBar) findViewById(R.id.p_loading);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String query = null;

        if(bundle != null){
            query =  bundle.getString("Query");
        }


        URL bookURl;
        try {
            if (query == null || query.isEmpty()) {
                bookURl = buildUri("cooking");
            } else {
                bookURl = new URL(query);
            }
            //String jsonResult = ApiUtil.getJson(bookURl);
            new BooksQueryTask().execute(bookURl);
        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }

        LinearLayoutManager booksLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        rvBooks.setLayoutManager(booksLayoutManager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_list_menu, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        ArrayList<String> recentList = SpUtil.getQueryList(getApplicationContext());
        int itemNum = recentList.size();
        MenuItem recentMenu;
        for(int i=0; i<itemNum; i++){
            recentMenu = menu.add(Menu.NONE, i, Menu.NONE, recentList.get(i));

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_advanced_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                return true;
            default:
                int position = item.getItemId() + 1;
                String preferenceName = SpUtil.QUERY + String.valueOf(position);
                String query = SpUtil.getPreferenceString(getApplicationContext(), preferenceName);
                String[] prefParams = query.split("\\,");
                String[] queryParams = new String[4];

                for(int i =0; i<prefParams.length; i++){
                    queryParams[i] = prefParams[i];
                }

                URL bookUrl = ApiUtil.buildUri(
                        (queryParams[0] == null) ? "" : queryParams[0],
                        (queryParams[1] == null) ? "" : queryParams[1],
                        (queryParams[2] == null) ? "" : queryParams[2],
                        (queryParams[3] == null) ? "" : queryParams[3]
                );

                Intent intent1 = new Intent(getApplicationContext(), BookListActivity.class);
                intent1.putExtra("Query", bookUrl.toString());
                startActivity(intent1);

                //return super.onOptionsItemSelected(item);
                return true;
        }


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        try {
            URL bookUrl = ApiUtil.buildUri(query);
            new BooksQueryTask().execute(bookUrl);

        } catch (Exception e) {
            Log.d("Error", e.getMessage());
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    public class BooksQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls) {
            URL searchURL = urls[0];
            String result = null;
            try {
                result = ApiUtil.getJson(searchURL);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            //TextView tvResult = (TextView) findViewById(R.id.tvResponse);
            TextView tvError = (TextView) findViewById(R.id.tv_error);
            mLoadingProgress.setVisibility(View.INVISIBLE);
            if (result == null) {
                //tvResult.setVisibility(View.INVISIBLE);
                rvBooks.setVisibility(View.INVISIBLE);
                tvError.setVisibility(View.VISIBLE);
            } else {

                //tvResult.setVisibility(View.VISIBLE);
                rvBooks.setVisibility(View.VISIBLE);
                tvError.setVisibility(View.INVISIBLE);

                ArrayList<Book> books = ApiUtil.getBooksFromJson(result);
                //String resultString = "";
                //for (Book book : books) {
                //resultString = resultString + book.title + "\n" + book.publishedDate + "\n\n";
                //}

                //tvResult.setText(resultString);
                BooksAdapter adapter = new BooksAdapter(books);
                rvBooks.setAdapter(adapter);
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingProgress.setVisibility(View.VISIBLE);
        }
    }
}