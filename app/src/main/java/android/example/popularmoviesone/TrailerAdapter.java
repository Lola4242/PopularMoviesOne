package android.example.popularmoviesone;

import android.content.Context;
import android.example.popularmoviesone.model.Trailer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class TrailerAdapter extends BaseAdapter {

    private Context mContext;
    private final List<Trailer> trailers;

    public TrailerAdapter(Context c, List<Trailer> trailers){
        this.mContext = c;
        this.trailers = trailers;
    }

    @Override
    public int getCount() {
        return trailers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.trailer_list_item, null);
        }

        final TextView textView = (TextView) convertView.findViewById(R.id.trailer_tv);
        textView.setText("Trailer " + (position + 1));


        return convertView;
    }
}
