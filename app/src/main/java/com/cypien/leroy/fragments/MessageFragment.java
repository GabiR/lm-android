package com.cypien.leroy.fragments;/*
 * Created by Alex on 26.09.2016.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Message;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class MessageFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.message_fragment, container, false);


        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Message"));
        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Message");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Bundle bundle = this.getArguments();
        Message message = (Message) bundle.getSerializable("message");
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView date = (TextView) view.findViewById(R.id.date);
        title.setText(message.getTitle());
        content.setText(message.getContent());
        date.setText(message.getDate());
        return view;
    }
}
