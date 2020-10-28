package fr.insalyon.mxyns.icrc.dna.case_list;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import fr.insalyon.mxyns.icrc.dna.R;

public class CaseRecyclerViewAdapter extends RecyclerView.Adapter<CaseRecyclerViewAdapter.ViewHolder> {

    private final List<CaseItemContent> mValues;

    public CaseRecyclerViewAdapter(List<CaseItemContent> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_case_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        holder.mContentView.setText(mValues.get(position).displayName);
        holder.caseStatus.setBackgroundTintList(ColorStateList.valueOf(holder.mItem.getColor()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final EditText mContentView;
        public final MaterialButton mEdit;
        public final View caseStatus;
        private final Drawable defaultBackground;
        public CaseItemContent mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            caseStatus = view.findViewById(R.id.case_status);

            defaultBackground = mContentView.getBackground();
            mContentView.setInputType(InputType.TYPE_NULL);
            mContentView.setBackground(null);

            mEdit = view.findViewById(R.id.edit);

            // on click on edit => can edit text
            // on click on confirm => save text
            mEdit.setOnClickListener(e -> {

                if (mEdit.getTag().toString().equalsIgnoreCase("edit")) {
                    mContentView.setInputType(InputType.TYPE_CLASS_TEXT);
                    mContentView.setBackground(defaultBackground);

                    mEdit.setTag("confirm");
                    mEdit.setIconResource(R.drawable.ic_baseline_check_24);
                } else {
                    mContentView.setInputType(InputType.TYPE_NULL);
                    mContentView.setBackground(null);

                    mContentView.setText(mItem.setDisplayName(mContentView.getText().toString()));

                    mEdit.setTag("edit");
                    mEdit.setIconResource(R.drawable.ic_baseline_edit_24);
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}