package fr.insalyon.mxyns.icrc.dna.case_list;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.insalyon.mxyns.icrc.dna.R;

/**
 * A fragment representing a list of Items.
 */
public class CaseListFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CaseListFragment() {
    }

    // TODO: Customize parameter initialization
    public static CaseListFragment newInstance() {
        CaseListFragment fragment = new CaseListFragment();
        // insert data using Bundle
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // load infos using getArguments()
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_case_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CaseRecyclerViewAdapter(CaseItemContent.ITEMS));
        }
        return view;
    }
}