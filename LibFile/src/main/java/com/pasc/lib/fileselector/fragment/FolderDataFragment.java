package com.pasc.lib.fileselector.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pasc.lib.fileselector.adapter.FolderDataRecycleAdapter;
import com.pasc.lib.fileselector.model.FileInfo;
import com.pasc.lib.fileselector.utils.RecyclerViewClickSupport;
import com.pasc.pasc.lib.file.R;

import java.util.List;

public class FolderDataFragment extends Fragment {

    private RecyclerView rvDoc;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_doc, container, false);
        rvDoc = rootView.findViewById(R.id.rv_doc);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    private void initData() {
        Bundle bundle = this.getArguments();

        final List<FileInfo> data = bundle.getParcelableArrayList("file_data");
        boolean isImage = bundle.getBoolean("is_image");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        //设置RecyclerView 布局
        rvDoc.setLayoutManager(linearLayoutManager);
        FolderDataRecycleAdapter pptListAdapter = new FolderDataRecycleAdapter(getActivity(), data, isImage);
        rvDoc.setAdapter(pptListAdapter);

        RecyclerViewClickSupport mRCClickSupport = new RecyclerViewClickSupport(rvDoc, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = rvDoc.getChildLayoutPosition(v);
                sendData(data.get(position).getFilePath());
            }
        }, new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }

    public void sendData(String filePath){
        Intent data = getActivity().getIntent().putExtra("selectFile",filePath);
        getActivity().setResult(1,data);
        getActivity().finish();
    }
}
