package me.li2.android.architecture.ui.list;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.DaggerFragment;
import dagger.android.support.FragmentKey;
import me.li2.android.architecture.R;
import me.li2.android.architecture.data.model.Article;
import me.li2.android.architecture.ui.basic.RecyclerViewMarginDecoration;
import me.li2.android.architecture.ui.detail.ArticleDetailActivity;
import me.li2.android.architecture.utils.InjectorUtils;
import me.li2.android.architecture.utils.NetworkUtils;
import me.li2.android.architecture.utils.NoNetworkException;

public class ArticleListFragment extends DaggerFragment implements ArticleSelectListener {
    private static final String LOG_TAG = ArticleListFragment.class.getSimpleName();
    private static final String BUNDLE_RECYCLER_POSITION = "recycler_position";

    @Inject
    ArticleListAdapter mAdapter;

    @Inject
    ArticleListFragmentViewModel mViewModel;

    @BindView(R.id.article_list_view)
    RecyclerView mRecyclerView;

    @BindView(R.id.article_list_swiperefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    public ArticleListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_RECYCLER_POSITION,
                ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.article_list_fragment, container, false);
        ButterKnife.bind(this, view);

        final RecyclerView recyclerView = mRecyclerView;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setScrollContainer(false);
        recyclerView.setNestedScrollingEnabled(false);
        // setup RecyclerView item margin
        int margin = (int)getResources().getDimension(R.dimen.default_margin);
        recyclerView.addItemDecoration(new RecyclerViewMarginDecoration(margin));
        // setup adapter
        recyclerView.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            // notebyweiyi: IllegalArgumentException: Invalid target position -1, scrollToPosition() not works
            int position = savedInstanceState.getInt(BUNDLE_RECYCLER_POSITION);
            if (position > 0) {
                mRecyclerView.smoothScrollToPosition(position);
            }
        }
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getContext().registerReceiver(mConnectivityChangeReceiver, NetworkUtils.connectivityChangeFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getContext().unregisterReceiver(mConnectivityChangeReceiver);
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            if (!NetworkUtils.isConnected()) {
                showMessage(R.string.status_no_connect);
            } else {
                loadData();
            }
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };

    private BroadcastReceiver mConnectivityChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (NetworkUtils.isConnectivityChangeAction(intent.getAction())) {
                showMessage(!NetworkUtils.isConnected() ? R.string.status_no_connect : R.string.status_connected);
            }
        }
    };

    private void loadData() {
        mViewModel.getArticleList().observe(getActivity(), resource -> {
            Log.d(LOG_TAG, "loading status: " + resource.status + ", code " + resource.code);

            switch (resource.status) {
                case LOADING:
                    showMessage(R.string.status_loading);
                    break;

                case SUCCESS:
                    showMessage(R.string.status_success);
                    if (resource.data == null) {
                        showMessage(R.string.status_no_response);
                    }
                    break;

                case ERROR:
                    if (resource.throwable instanceof NoNetworkException) {
                        showMessage(R.string.status_no_connect);
                    }
                    break;
            }

            // update recycler view
            mAdapter.update(resource.data);
        });
    }

    private void showMessage(int stringResId) {
        Snackbar.make(getView(), stringResId, Snackbar.LENGTH_LONG)
                .show();
    }

    // Delegate ViewHolder click event to Fragment, then we can use activity to implement transition animation.
    @Override
    public void onArticleSelect(Article article, View sharedView, String sharedName) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(getActivity(), sharedView, sharedName);
        startActivity(ArticleDetailActivity.newIntent(getContext(), article.getId()), options.toBundle());
    }
}
