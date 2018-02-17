package com.ventoray.shaut.client_data;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

public class FriendRequestPullService extends JobService {
    private AsyncTask friendRequestAsyncTask;

    public FriendRequestPullService() {
    }

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        friendRequestAsyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Context context = FriendRequestPullService.this;
                DataTasks.executeTask(context, DataTasks.ACTION_PULL_FRIEND_REQUESTS);
                return null;
            }

            /**
             * Send out broadcast intent to appwidget
             * @param o
             */
            @Override
            protected void onPostExecute(Object o) {


                jobFinished(jobParameters,false);
            }
        };


        friendRequestAsyncTask.execute();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (friendRequestAsyncTask != null) friendRequestAsyncTask.cancel(true);
        return true;
    }
}
