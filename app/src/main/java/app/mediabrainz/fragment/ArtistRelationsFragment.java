package app.mediabrainz.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import app.mediabrainz.R;
import app.mediabrainz.adapter.recycler.artistRelations.ArtistRelationsAdapter;
import app.mediabrainz.adapter.recycler.artistRelations.Header;
import app.mediabrainz.adapter.recycler.expandedRecycler.Section;
import app.mediabrainz.api.model.relations.ArtistArtistRelationType;
import app.mediabrainz.api.model.relations.Relation;
import app.mediabrainz.viewmodel.ArtistRelationsVM;
import app.mediabrainz.viewmodel.event.ArtistRelationsEvent;

import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.COLLABORATION;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.COMPOSER_IN_RESIDENCE;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.CONDUCTOR_POSITION;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.FOUNDER;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.INSTRUMENTAL_SUPPORTING_MUSICIAN;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.IS_PERSON;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.MEMBER_OF_BAND;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.SUBGROUP;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.SUPPORTING_MUSICIAN;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.TEACHER;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.TRIBUTE;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.VOCAL_SUPPORTING_MUSICIAN;
import static app.mediabrainz.api.model.relations.ArtistArtistRelationType.VOICE_ACTOR;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.COLLABORATIONS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.COMPOSER_IN_RESIDENCES;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.CONDUCTOR_POSITIONS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.CURRENT_MEMBER_OF_BANDS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.FOUNDERS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.INSTRUMENTAL_SUPPORTING_MUSICIANS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.IS_PERSONS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.PAST_MEMBER_OF_BANDS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.SUBGROUPS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.SUPPORTING_MUSICIANS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.TEACHERS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.TRIBUTES;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.VOCAL_SUPPORTING_MUSICIANS;
import static app.mediabrainz.fragment.ArtistRelationsFragment.RelationshipsType.VOICE_ACTORS;


public class ArtistRelationsFragment extends BaseComplexRecyclerFragment<Relation> {

    public enum RelationshipsType {
        CURRENT_MEMBER_OF_BANDS(MEMBER_OF_BAND, R.string.rel_cuttent_member_of_band, R.string.rel_current_member_of_band_description),
        PAST_MEMBER_OF_BANDS(MEMBER_OF_BAND, R.string.rel_past_member_of_band, R.string.rel_past_member_of_band_description),
        SUBGROUPS(SUBGROUP, R.string.rel_subgroup, R.string.rel_subgroup_description),
        CONDUCTOR_POSITIONS(CONDUCTOR_POSITION, R.string.rel_conductor_position, R.string.rel_conductor_position_description),
        FOUNDERS(FOUNDER, R.string.rel_founder, R.string.rel_founder_description),
        SUPPORTING_MUSICIANS(SUPPORTING_MUSICIAN, R.string.rel_supporting_musician, R.string.rel_supporting_musician_description),
        VOCAL_SUPPORTING_MUSICIANS(VOCAL_SUPPORTING_MUSICIAN, R.string.rel_vocal_supporting_musician, R.string.rel_vocal_supporting_musician_description),
        INSTRUMENTAL_SUPPORTING_MUSICIANS(INSTRUMENTAL_SUPPORTING_MUSICIAN, R.string.rel_instrumental_supporting_musician, R.string.rel_instrumental_supporting_musician_description),
        TRIBUTES(TRIBUTE, R.string.rel_tribute, R.string.rel_tribute_description),
        VOICE_ACTORS(VOICE_ACTOR, R.string.rel_voice_actor, R.string.rel_voice_actor_description),
        COLLABORATIONS(COLLABORATION, R.string.rel_collaboration, R.string.rel_collaboration_description),
        IS_PERSONS(IS_PERSON, R.string.rel_is_person, R.string.rel_is_person_description),
        TEACHERS(TEACHER, R.string.rel_teacher, R.string.rel_teacher_description),
        COMPOSER_IN_RESIDENCES(COMPOSER_IN_RESIDENCE, R.string.rel_composer_in_residence, R.string.rel_composer_in_residence_description);

        private final ArtistArtistRelationType type;
        private final int relationResource;
        private final int descriptionResource;

        RelationshipsType(ArtistArtistRelationType type, int relationResource, int descriptionResource) {
            this.type = type;
            this.relationResource = relationResource;
            this.descriptionResource = descriptionResource;
        }

        public String getType() {
            return type.toString();
        }

        public int getRelationResource() {
            return relationResource;
        }

        public int getDescriptionResource() {
            return descriptionResource;
        }
    }

    private View noresultsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = super.onCreateView(inflater, container, savedInstanceState);

        recyclerContainerView.setVisibility(View.INVISIBLE);
        View frame = inflater.inflate(R.layout.artist_relations_fragment, null);
        noresultsView = frame.findViewById(R.id.noresultsView);
        addFrameView(frame);

        initSections();
        return layout;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null && getArguments() != null) {
            ArtistRelationsFragmentArgs args = ArtistRelationsFragmentArgs.fromBundle(getArguments());
            setSubtitle(args.getSubTitle());

            ArtistRelationsVM artistRelationsVM = getViewModel(ArtistRelationsVM.class);
            getActivityViewModel(ArtistRelationsEvent.class).relations.observe(this, relations -> {
                if (relations != null) artistRelationsVM.relationsld.setValue(relations);
            });
            artistRelationsVM.relationsld.observe(this, this::show);
        }
    }

    private void initSections() {
        allSections = new ArrayList<>();
        for (ArtistRelationsFragment.RelationshipsType type : ArtistRelationsFragment.RelationshipsType.values()) {
            Header header = new Header();
            header.setTitle(getString(type.getRelationResource()));
            header.setDescription(getString(type.getDescriptionResource()));
            allSections.add(new Section<>(header));
        }
    }

    protected void show(List<Relation> relations) {
        recyclerView.removeAllViewsInLayout();

        Comparator<Relation> sortDate = (r1, r2) -> (r1.getArtist().getName()).compareTo(r2.getArtist().getName());
        Collections.sort(relations, sortDate);
        clearSections();
        for (Relation relation : relations) {
            String type = !TextUtils.isEmpty(relation.getType()) ? relation.getType() : "";
            if (type.equalsIgnoreCase(CURRENT_MEMBER_OF_BANDS.getType())) {
                if (TextUtils.isEmpty(relation.getEnd())) {
                    allSections.get(CURRENT_MEMBER_OF_BANDS.ordinal()).getItems().add(relation);
                } else {
                    allSections.get(PAST_MEMBER_OF_BANDS.ordinal()).getItems().add(relation);
                }
            } else if (type.equalsIgnoreCase(SUBGROUPS.getType())) {
                allSections.get(SUBGROUPS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(CONDUCTOR_POSITIONS.getType())) {
                allSections.get(CONDUCTOR_POSITIONS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(FOUNDERS.getType())) {
                allSections.get(FOUNDERS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(SUPPORTING_MUSICIANS.getType())) {
                allSections.get(SUPPORTING_MUSICIANS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(VOCAL_SUPPORTING_MUSICIANS.getType())) {
                allSections.get(VOCAL_SUPPORTING_MUSICIANS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(INSTRUMENTAL_SUPPORTING_MUSICIANS.getType())) {
                allSections.get(INSTRUMENTAL_SUPPORTING_MUSICIANS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(TRIBUTES.getType())) {
                allSections.get(TRIBUTES.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(VOICE_ACTORS.getType())) {
                allSections.get(VOICE_ACTORS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(COLLABORATIONS.getType())) {
                allSections.get(COLLABORATIONS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(IS_PERSONS.getType())) {
                allSections.get(IS_PERSONS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(TEACHERS.getType())) {
                allSections.get(TEACHERS.ordinal()).getItems().add(relation);
            } else if (type.equalsIgnoreCase(COMPOSER_IN_RESIDENCES.getType())) {
                allSections.get(COMPOSER_IN_RESIDENCES.ordinal()).getItems().add(relation);
            }
        }
            /*
            if (!allSections.get(CURRENT_MEMBER_OF_BANDS.ordinal()).getItems().isEmpty()) {
                allSections.get(CURRENT_MEMBER_OF_BANDS.ordinal()).getHeader().setExpand(true);
            }
            */
        viewSections = new ArrayList<>();
        for (Section<Relation> section : allSections) {
            if (!section.getItems().isEmpty()) {
                viewSections.add(section);
            }
        }

        if (!viewSections.isEmpty()) {
            configRecycler(viewSections);
        }
        showNoResult(viewSections.isEmpty());
    }

    private void configRecycler(List<Section<Relation>> items) {
        restoreRecyclerToolbarState(items);

        ArtistRelationsAdapter artistRelationsAdapter = new ArtistRelationsAdapter(items);
        recyclerAdapter = artistRelationsAdapter;
        artistRelationsAdapter.setOnItemClickListener(artist -> {
            ArtistRelationsFragmentDirections.ActionArtistRelationsFragmentToArtistFragment action =
                    ArtistRelationsFragmentDirections.actionArtistRelationsFragmentToArtistFragment(artist.getId());
            navigate(action);
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(artistRelationsAdapter);

        configRecyclerToolbar();
        expandCheckBox.setChecked(true);
    }

    private void showNoResult(boolean show) {
        noresultsView.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerContainerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

}
