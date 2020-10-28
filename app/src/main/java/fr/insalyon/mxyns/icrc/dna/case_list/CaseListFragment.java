package fr.insalyon.mxyns.icrc.dna.case_list;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Objects;

import fr.insalyon.mxyns.icrc.dna.R;
import fr.insalyon.mxyns.icrc.dna.utils.FileUtils;

public class CaseListFragment extends Fragment {

    ArrayList<CaseItemContent> items = new ArrayList<>();

    public CaseListFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        items.clear();
        Activity activity = requireActivity();

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
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new CaseRecyclerViewAdapter(items));
        }
        return view;
    }
}