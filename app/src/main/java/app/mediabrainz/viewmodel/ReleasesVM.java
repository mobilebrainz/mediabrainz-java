package app.mediabrainz.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import app.mediabrainz.api.browse.ReleaseBrowseService;
import app.mediabrainz.api.model.Release;
import app.mediabrainz.core.viewmodel.CompositeDisposableViewModel;
import app.mediabrainz.core.viewmodel.NetworkState;
import app.mediabrainz.data.datasource.ReleasesDataSource;

import static app.mediabrainz.data.datasource.ReleasesDataSource.RELEASE_BROWSE_LIMIT;


public class ReleasesVM extends CompositeDisposableViewModel {

    public LiveData<PagedList<Release>> realesesLiveData;
    private MutableLiveData<ReleasesDataSource> releasesDataSourceMutableLiveData;

    public void load(String mbid, ReleaseBrowseService.ReleaseBrowseEntityType type) {
        ReleasesDataSource.Factory factory = new ReleasesDataSource.Factory(compositeDisposable, mbid, type);
        PagedList.Config config = new PagedList.Config.Builder()
                .setPageSize(RELEASE_BROWSE_LIMIT)
                //.setInitialLoadSizeHint(PAGE_SIZE)
                .setEnablePlaceholders(false)
                .build();

        realesesLiveData = new LivePagedListBuilder<>(factory, config).build();
        releasesDataSourceMutableLiveData = factory.getReleasesDataSourceLiveData();
    }

    public void retry() {
        releasesDataSourceMutableLiveData.getValue().retry();
    }

    public void refresh() {
        releasesDataSourceMutableLiveData.getValue().invalidate();
    }

    public LiveData<NetworkState> getNetworkState() {
        return Transformations.switchMap(releasesDataSourceMutableLiveData, ReleasesDataSource::getNetworkState);
    }

    public LiveData<NetworkState> getRefreshState() {
        return Transformations.switchMap(releasesDataSourceMutableLiveData, ReleasesDataSource::getInitialLoad);
    }

}
