package com.cypien.leroy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.ProjectsAdapter;
import com.cypien.leroy.models.Project;
import com.cypien.leroy.utils.Connections;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Alex on 21/10/15.
 */
public class ProjectsListFragment extends Fragment {
    private View view;
    private ProjectsAdapter adapter;
    private ListView list;
    private Button seeMoreButton;
    private SharedPreferences sp;

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() + 10));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.projects_list, container, false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Projects"));
        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Projects");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Proiecte");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);


        seeMoreButton = (Button) view.findViewById(R.id.see_more);
        list = (ListView) view.findViewById(R.id.projects_list);
        adapter = new ProjectsAdapter(getActivity());
        list.setAdapter(adapter);

        loadPage();


        setListViewHeightBasedOnChildren(list);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProjectFragment projectFragment = new ProjectFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("project", adapter.getItem(position));
                bundle.putInt("position", position);
                projectFragment.setArguments(bundle);
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.content_frame_community, projectFragment, "project").addToBackStack(null);
                transaction.commit();
            }
        });

        seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LeroyApplication.megabytesFree() > 30) {
                    getProjects(adapter.getCount());
                    adapter.notifyDataSetChanged();
                    setListViewHeightBasedOnChildren(list);
                } else {
                    v.setVisibility(View.GONE);
                }
            }
        });


        return view;
    }

    private void loadPage() {
        if (Connections.isNetworkConnected(getActivity())) {
            getProjects(adapter.getCount());
        } else {
            Type myObjectType = new TypeToken<Integer>() {
            }.getType();
            Integer nrProjects = (Integer) LeroyApplication.getCacheManager().get("projects_nr", Integer.class, myObjectType);
            myObjectType = new TypeToken<Project>() {
            }.getType();
            if (nrProjects != null) {
                for (int i = 0; i < nrProjects; i++) {
                    Project project = (Project) LeroyApplication.getCacheManager().get("project_" + i, Project.class, myObjectType);
                    adapter.add(project);
                }
            }
        }
    }

    private void getProjects(int a) {
        try {

            JSONObject response = LeroyApplication.getInstance().makeRequest("project_get_combined", sp.getString("endpointCookie", ""), sp.getString("userid", ""), "" + a, "10");
            JSONArray resultArray = response.getJSONArray("result");
            if (resultArray.length() > 0) {
                for (int i = 0; i < resultArray.length(); i++) {
                    Project project = new Project();
                    project = project.fromJson(resultArray.getJSONObject(i));
                    LeroyApplication.getCacheManager().put("project_" + (a + i), project);
                    adapter.add(project);
                    LeroyApplication.getCacheManager().put("projects_nr", adapter.getCount());
                }
            } else
                seeMoreButton.setVisibility(View.GONE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
