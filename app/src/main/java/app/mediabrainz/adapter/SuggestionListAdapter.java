package app.mediabrainz.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import app.mediabrainz.R;
import app.mediabrainz.data.room.entity.Suggestion;
import app.mediabrainz.data.room.repository.SuggestionRepository;


@SuppressWarnings("unchecked")
public class SuggestionListAdapter extends ArrayAdapter {

    private static final int LAYOUT_ID = R.layout.layout_dropdown_item;

    private List<Suggestion> dataList;
    private Suggestion.SuggestionField suggestionField;

    private SuggestionRepository suggestionRepository = new SuggestionRepository();
    private SuggestionListAdapter.ListFilter listFilter = new SuggestionListAdapter.ListFilter();

    public SuggestionListAdapter(@NonNull Context context, @NonNull Suggestion.SuggestionField suggestionField) {
        this(context, new ArrayList<>(), suggestionField);
    }

    private SuggestionListAdapter(@NonNull Context context, @NonNull List<Suggestion> dataList, @NonNull Suggestion.SuggestionField suggestionField) {
        super(context, LAYOUT_ID, dataList);
        this.dataList = dataList;
        this.suggestionField = suggestionField;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Suggestion getItem(int position) {
        return dataList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        if (view == null) {
            view = LayoutInflater.from(parent.getContext()).inflate(LAYOUT_ID, parent, false);
        }
        TextView dropdownItemView = view.findViewById(R.id.dropdownItemView);
        dropdownItemView.setText(getItem(position).getWord());
        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    public class ListFilter extends Filter {
        private final Object lock = new Object();

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    results.values = new ArrayList<String>();
                    results.count = 0;
                }
            } else {
                final String searchStrLowerCase = prefix.toString().toLowerCase();
                List<Suggestion> matchValues = suggestionRepository.getSuggestions(searchStrLowerCase, suggestionField.getField());
                results.values = matchValues;
                results.count = matchValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.values != null) {
                dataList = (ArrayList<Suggestion>) results.values;
            } else {
                dataList = null;
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
