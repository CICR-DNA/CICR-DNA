package fr.insalyon.mxyns.icrc.dna.case_list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

/**
 * Case List in MainActivity
 *
 * @see fr.insalyon.mxyns.icrc.dna.MainActivity
 */
public class CaseListFragment extends Fragment {

    /**
     * Cases data
     */
    ArrayList<CaseItemContent> items = new ArrayList<>();

    /**
     *
     */

    private RecyclerView recyclerView;

    public CaseListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items.clear();
        Activity activity = requireActivity();

        // load cases from files
        String dir_path = activity.getFilesDir().getPath() + activity.getResources().getString(R.string.files_path);
        Log.d("loading-json", "loading files in dir " + dir_path);

        for (File file : FileUtils.listFiles(dir_path)) {
            try {
                items.add(CaseItemContent.fromFile(getContext(), file));
            } catch (Exception ignored) {
                Log.d("loading-json", "error while loading file : " + file);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CaseRecyclerViewAdapter(items));
            recyclerView.addItemDecoration(new DividerItemDecoration(context,DividerItemDecoration.VERTICAL));
        }
        return view;
    }

    public void setMultiSelection(boolean state) {

        if (this.recyclerView != null) {
            for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
                CaseRecyclerViewAdapter.ViewHolder viewHolder = (CaseRecyclerViewAdapter.ViewHolder) this.recyclerView.findViewHolderForAdapterPosition(i);
                viewHolder.setMultiSelectionMode(state);
            }
        }
    }

    public ArrayList<String> getSelectedPaths() {

        ArrayList<String> selected = new ArrayList<>();
        if (this.recyclerView != null) {
            for (int i = 0; i < this.recyclerView.getChildCount(); i++) {
                CaseRecyclerViewAdapter.ViewHolder viewHolder = (CaseRecyclerViewAdapter.ViewHolder) this.recyclerView.findViewHolderForAdapterPosition(i);
                if (viewHolder != null && viewHolder.mMultiSelectionCheckbox.isChecked())
                    selected.add(viewHolder.mItem.path);
            }
        }

        return selected;
    }
}