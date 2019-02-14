package app.mediabrainz.core.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import app.mediabrainz.api.model.ReleaseGroup;
import app.mediabrainz.data.ReleaseGroupsDataSource;

import static app.mediabrainz.data.ReleaseGroupsDataSource.RELEASE_GROUPE_BROWSE_LIMIT;


public class ReleaseGroupsVM extends CompositeDisposableViewModel {

    public LiveData<PagedList<ReleaseGroup>> realeseGroupLiveData;
    private MutableLiveData<ReleaseGroupsDataSource> releaseGroupsDataSourceMutableLiveData;

    public void load(String artistMbid, ReleaseGroup.AlbumType albumType, MutableLiveData<Boolean> mutableIsOfficial) {
        ReleaseGroupsDataSource.Factory factory = new ReleaseGroupsDataSource.Factory(compositeDisposable, artistMbid, albumType, mutableIsOfficial);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(RELEASE_GROUPE_BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        realeseGroupLiveData = new LivePagedListBuilder<>(factory, config).build();
        releaseGroupsDataSourceMutableLiveData = factory.getReleaseGroupsDataSourceLiveData();
    }

    public void retry() {
        releaseGroupsDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        releaseGroupsDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(releaseGroupsDataSourceMutableLiveData, ReleaseGroupsDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(releaseGroupsDataSourceMutableLiveData, ReleaseGroupsDataSource::getInitialLoad);
    }

}
