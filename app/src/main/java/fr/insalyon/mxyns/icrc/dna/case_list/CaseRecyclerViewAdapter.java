package fr.insalyon.mxyns.icrc.dna.case_list;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fr.insalyon.mxyns.icrc.dna.DataGatheringActivity;
import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

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

        holder.mContentView.setEnabled(false);
        holder.mContentView.setEnabled(true);

        holder.mContentView.setText(mValues.get(position).displayName);
        holder.caseStatus.setBackgroundTintList(ColorStateList.valueOf(holder.mItem.getColor()));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public boolean deleteCase(int position) {


        boolean result = FileUtils.deleteFile(mValues.get(position).path);
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());

        return result;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final EditText mContentView;
        public final ImageButton mMenu;
        public final View caseStatus;
        private final Drawable defaultBackground;
        public CaseItemContent mItem;

        public ViewHolder(View view) {
            super(view);

            mView = view;

            mContentView = view.findViewById(R.id.case_item_name);

            caseStatus = view.findViewById(R.id.case_item_status);

            mMenu = view.findViewById(R.id.case_item_menu_button);

            defaultBackground = mContentView.getBackground();

            renameMode(false);

            // on click on edit => can edit text
            // on click on confirm => save text
            mMenu.setOnClickListener(e -> showMenu());

            mContentView.setOnFocusChangeListener( (v, hasFocus) -> {
                if (!hasFocus) {
                    renameMode(false);
                }
            });
            mContentView.setOnEditorActionListener((v, actionId, event) -> actionId == EditorInfo.IME_ACTION_DONE && !renameMode(false));

            caseStatus.setOnClickListener(this::openFile);
            mContentView.setOnClickListener(e -> {

                Log.d("menu-rename", "click");
                Log.d("menu-rename", String.valueOf(mContentView.getTag()));

                if (mContentView.getTag().toString().equalsIgnoreCase("editable=false"))
                    openFile(mView);
            });
        }

        private void openFile(View view) {

            Log.d("menu-rename", "open file " + mItem.path);
            Context context = view.getContext();
            Intent intent = new Intent(context, DataGatheringActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            intent.putExtra("load", mItem.path);
            intent.putExtra("plsreset", true);

            context.startActivity(intent);
        }

        private void showMenu() {

            PopupMenu menu = new PopupMenu(mView.getContext(), mMenu);
            menu.inflate(R.menu.case_item_menu);
            menu.setOnMenuItemClickListener(item -> {

                int id = item.getItemId();
                if (id == R.id.menu_rename)
                    return renameMode(true);
                else if (id == R.id.menu_delete)
                    return deleteCase(getAdapterPosition());
                else if (id == R.id.menu_edit) {
                    openFile(mView);
                    return true;
                }

                return false;
            });
            menu.show();
        }

        private boolean renameMode(boolean state) {

            Log.d("menu-rename", "rename " + state);

            if (state) {
                mContentView.setInputType(InputType.TYPE_CLASS_TEXT);
                mContentView.setBackground(defaultBackground);
                mContentView.setFocusableInTouchMode(true);
                mContentView.requestFocus();
            } else {
                mContentView.setInputType(InputType.TYPE_NULL);
                mContentView.setFocusableInTouchMode(false);
                mContentView.setBackground(null);
                if (mItem != null)
                    mContentView.setText(mItem.setDisplayName(mContentView.getText().toString()));
            }
            mContentView.setTag("editable="+state);

            return state;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}