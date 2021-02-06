package org.itsallcode.whiterabbit.logic.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.ArrayList;

import org.itsallcode.whiterabbit.api.model.ActivityData;
import org.itsallcode.whiterabbit.logic.model.json.JsonActivity;
import org.itsallcode.whiterabbit.logic.model.json.JsonDay;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DayActivitiesTest
{
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private ContractTermsService contractTermsMock;
    private JsonDay jsonDay;
    private DayRecord dayRecord;

    @BeforeEach
    void setup()
    {
        jsonDay = new JsonDay();
    }

    @Test
    void addWhenActivitiesNotNull()
    {
        final DayActivities activities = create();
        jsonDay.setActivities(new ArrayList<>());

        final Activity newActivity = activities.add();

        assertThat(newActivity).isNotNull();
        assertThat(newActivity.getProject()).isNull();
        assertThat(newActivity.getComment()).isNull();
        assertThat(newActivity.getDuration()).isZero();
        assertThat(newActivity.getRow()).isZero();

        assertThat(jsonDay.getActivities()).hasSize(1);
    }

    @Test
    void addWhenActivitiesAreNull()
    {
        final DayActivities activities = create();
        jsonDay.setActivities(null);

        final Activity newActivity = activities.add();

        assertThat(newActivity).isNotNull();
        assertThat(newActivity.getProject()).isNull();
        assertThat(newActivity.getComment()).isNull();
        assertThat(newActivity.getDuration()).isZero();
        assertThat(newActivity.getRow()).isZero();

        assertThat(jsonDay.getActivities()).hasSize(1);
    }

    @Test
    void firstAddedActivityHasRemainderFlag()
    {
        final DayActivities activities = create();
        jsonDay.setActivities(new ArrayList<>());

        final Activity newActivity = activities.add();

        assertThat(newActivity.isRemainderActivity()).as("remainder").isTrue();
    }

    @Test
    void secondAddedActivityDoesNotHaveRemainderFlag()
    {
        final DayActivities activities = create();
        jsonDay.setActivities(new ArrayList<>());

        activities.add();
        final Activity newActivity = activities.add();

        assertThat(newActivity.isRemainderActivity()).as("remainder").isFalse();
    }

    @Test
    void addIncrementsRowIndex()
    {
        final DayActivities activities = create();

        final Activity firstActivity = activities.add();
        final Activity secondActivity = activities.add();

        assertThat(firstActivity.getRow()).isZero();
        assertThat(secondActivity.getRow()).isEqualTo(1);
        assertThat(jsonDay.getActivities()).hasSize(2);
    }

    @Test
    void getAllEmpty()
    {
        final DayActivities activities = create();

        assertThat(activities.getAll()).isEmpty();
    }

    @Test
    void getAllNotEmpty()
    {
        final DayActivities activities = create();
        activities.add();

        assertThat(activities.getAll()).hasSize(1);
    }

    @Test
    void getByIndexReturnsEmptyForNonExistingIndex()
    {
        final DayActivities activities = create();
        activities.add();

        assertThat(activities.get(1)).isEmpty();
    }

    @Test
    void getByIndexReturnsActivityExistingIndex()
    {
        final DayActivities activities = create();
        activities.add();

        assertThat(activities.get(0)).isPresent();
        assertThat(activities.get(0).get().getRow()).isZero();
    }

    @Test
    void removeWhenActivitiesNull()
    {
        final DayActivities activities = create();
        jsonDay.setActivities(null);

        activities.remove(0);

        assertThat(activities.getAll()).isEmpty();
    }

    @Test
    void removeNonExistingIndexFails()
    {
        final DayActivities activities = create();
        activities.add();

        assertThatThrownBy(() -> activities.remove(1)).isInstanceOf(IndexOutOfBoundsException.class);
    }

    @Test
    void removeExistingIndex()
    {
        final DayActivities activities = create();
        activities.add();

        activities.remove(0);

        assertThat(activities.getAll()).isEmpty();
    }

    @Test
    void removeLastSetsActivitiesToNull()
    {
        final DayActivities activities = create();
        activities.add();

        assertThat(jsonDay.getActivities()).hasSize(1);

        activities.remove(0);

        assertThat(jsonDay.getActivities()).isNull();

        assertThat(activities.getAll()).isEmpty();
    }

    @Test
    void setRemainderActivityAlreadyNotRemainderUnchanged()
    {
        final DayActivities activities = create();
        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));

        final Activity activity = activities.add();
        activity.setDuration(null);

        activity.setRemainderActivity(true);

        assertThat(activity.isRemainderActivity()).isTrue();
        assertThat(activity.getDuration()).isEqualTo(Duration.ofHours(7));
    }

    @Test
    void setRemainderActivityAlreadyRemainderUnchanged()
    {
        final DayActivities activities = create();

        final Activity activity = activities.add();
        activity.setDuration(Duration.ZERO);

        activity.setRemainderActivity(false);

        assertThat(activity.isRemainderActivity()).isFalse();
        assertThat(activity.getDuration()).isZero();
    }

    @Test
    void setRemainderToTrue()
    {
        final DayActivities activities = create();

        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));

        final Activity activity = activities.add();
        activity.setDuration(Duration.ZERO);

        activity.setRemainderActivity(true);

        assertThat(activity.isRemainderActivity()).isTrue();
        assertThat(activity.getDuration()).isEqualTo(Duration.ofHours(7));
    }

    @Test
    void setRemainderToTrueResetsOtherActivitiesNoOtherRemainderActivites()
    {
        final DayActivities activities = create();
        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));

        final Activity firstActivity = activities.add();
        firstActivity.setRemainderActivity(false);
        firstActivity.setDuration(Duration.ofHours(1));

        final Activity activity = activities.add();
        activity.setDuration(Duration.ZERO);

        activity.setRemainderActivity(true);

        assertThat(activity.isRemainderActivity()).isTrue();
        assertThat(activity.getDuration()).isEqualTo(Duration.ofHours(6));

        assertThat(firstActivity.isRemainderActivity()).isFalse();
        assertThat(firstActivity.getDuration()).isEqualTo(Duration.ofHours(1));
    }

    @Test
    void setRemainderToTrueResetsOtherActivitiesOtherRemainderActivites()
    {
        final DayActivities activities = create();
        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));

        final Activity firstActivity = activities.add();
        firstActivity.setRemainderActivity(true);
        firstActivity.setDuration(Duration.ofHours(4));

        final Activity secondActivity = activities.add();
        secondActivity.setDuration(Duration.ofHours(2));

        secondActivity.setRemainderActivity(true);

        assertThat(secondActivity.isRemainderActivity()).isTrue();
        assertThat(secondActivity.getDuration()).isEqualTo(Duration.ofHours(3));

        assertThat(firstActivity.isRemainderActivity()).isFalse();
        assertThat(firstActivity.getDuration()).isEqualTo(Duration.ofHours(4));
    }

    @Test
    void setRemainderToFalse()
    {
        final DayActivities activities = create();

        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));

        final ActivityData activity = new JsonActivity();
        activity.setDuration(null);

        activities.setRemainderActivity(activity, false);

        assertThat(activity.isRemainder()).isFalse();
        assertThat(activity.getDuration()).isEqualTo(Duration.ofHours(7));
    }

    @Test
    void setRemainderToFalseSetsRemainingDuration()
    {
        final DayActivities activities = create();

        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));
        activities.add().setDuration(Duration.ofHours(3));

        final ActivityData activity = new JsonActivity();
        activity.setDuration(null);

        activities.setRemainderActivity(activity, false);

        assertThat(activity.isRemainder()).isFalse();
        assertThat(activity.getDuration()).isEqualTo(Duration.ofHours(4));
    }

    @Test
    void setRemainderToFalseSetsRemainingDurationMultipleActivities()
    {
        final DayActivities activities = create();

        when(contractTermsMock.getWorkingTime(same(dayRecord))).thenReturn(Duration.ofHours(7));
        activities.add().setDuration(Duration.ofHours(3));
        activities.add().setDuration(Duration.ofMinutes(30));

        final ActivityData activity = new JsonActivity();
        activity.setDuration(null);

        activities.setRemainderActivity(activity, false);

        assertThat(activity.isRemainder()).isFalse();
        assertThat(activity.getDuration()).isEqualTo(Duration.ofMinutes(30).plusHours(3));
    }

    private DayActivities create()
    {
        final DayRecord previousDay = null;
        final MonthIndex month = null;
        dayRecord = new DayRecord(contractTermsMock, jsonDay, previousDay, month, projectServiceMock);
        return new DayActivities(dayRecord, projectServiceMock);
    }
}
