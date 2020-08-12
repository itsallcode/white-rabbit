package org.itsallcode.whiterabbit.logic.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.itsallcode.whiterabbit.logic.model.MonthIndex;
import org.itsallcode.whiterabbit.logic.model.json.JsonMonth;
import org.itsallcode.whiterabbit.logic.service.contract.ContractTermsService;
import org.itsallcode.whiterabbit.logic.service.project.ProjectService;
import org.itsallcode.whiterabbit.logic.storage.Storage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActivityServiceTest
{
    private static final LocalDate DATE = LocalDate.of(2020, 8, 3);
    @Mock
    private Storage storageMock;
    @Mock
    private ContractTermsService contractTermsServiceMock;
    @Mock
    private AppServiceCallback appServiceCallbackMock;
    @Mock
    private ProjectService projectService;

    private ActivityService service;

    @BeforeEach
    void setUp()
    {
        service = new ActivityService(storageMock, appServiceCallbackMock);
    }

    @Test
    void addActivityFailsWhenMonthNotFound()
    {
        assertThatThrownBy(() -> service.addActivity(DATE)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void addActivity()
    {
        final MonthIndex month = createMonth();
        when(storageMock.loadMonth(YearMonth.from(DATE)))
                .thenReturn(Optional.of(month));

        assertThat(month.getDay(DATE).activities().getAll()).isEmpty();

        service.addActivity(DATE);

        verify(storageMock).storeMonth(same(month));
        verify(appServiceCallbackMock).recordUpdated(same(month.getDay(DATE)));

        assertThat(month.getDay(DATE).activities().getAll()).hasSize(1);
    }

    @Test
    void removeActivity()
    {
        final MonthIndex month = createMonth();
        when(storageMock.loadMonth(YearMonth.from(DATE)))
                .thenReturn(Optional.of(month));

        assertThat(month.getDay(DATE).activities().getAll()).isEmpty();

        service.addActivity(DATE);

        service.removeActivity(month.getDay(DATE).activities().get(0).get());

        verify(storageMock, times(2)).storeMonth(same(month));
        verify(appServiceCallbackMock, times(2)).recordUpdated(same(month.getDay(DATE)));

        assertThat(month.getDay(DATE).activities().getAll()).isEmpty();
    }

    private MonthIndex createMonth()
    {
        final JsonMonth monthRecord = new JsonMonth();
        monthRecord.setDays(new ArrayList<>());
        monthRecord.setYear(DATE.getYear());
        monthRecord.setMonth(DATE.getMonth());
        return MonthIndex.create(contractTermsServiceMock, monthRecord, projectService);
    }
}