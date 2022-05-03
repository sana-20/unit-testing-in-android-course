package com.techyourchance.testdrivendevelopment.exercise8;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactItemsUseCaseTest {

    FetchContactUseCase SUT;

    public static final String FILTER_TERM = "coco";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String IMAGE_URL = "imageUrl";
    public static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    public static final double AGE = 26.0;

    @Mock GetContactsHttpEndpoint mGetContactsHttpEndpointMock;
    @Mock FetchContactUseCase.Listener mListenerMock1;
    @Mock FetchContactUseCase.Listener mListenerMock2;

    @Captor ArgumentCaptor<List<Contact>> mAcListContacts;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactUseCase(mGetContactsHttpEndpointMock);
    }

    @Test
    public void fetchContactItems_filterTermPassedToEndPoint() throws Exception {
        // Arrange
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        // Act
        SUT.fetchCartItemsAndNotify(FILTER_TERM);
        // Assert
        verify(mGetContactsHttpEndpointMock).getContacts(acString.capture(), any(Callback.class));
        assertThat(acString.getValue(), is(FILTER_TERM));
    }

    @Test
    public void fetchContactItems_success_observersNotifiedWithCorrectData() throws Exception {
        // Arrange
        success();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onContactItemsFetched(mAcListContacts.capture());
        verify(mListenerMock2).onContactItemsFetched(mAcListContacts.capture());
        List<List<Contact>> captures = mAcListContacts.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1, is(getContact()));
        assertThat(capture2, is(getContact()));
    }

    @Test
    public void fetchContactItems_success_unsubscribedObserversNotNotified() throws Exception {
        // Arrange
        success();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onContactItemsFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);
    }

    @Test
    public void fetchContactItems_generalError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        generalError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onFetchCartItemsFailed();
        verify(mListenerMock2).onFetchCartItemsFailed();
    }

    @Test
    public void fetchContactItems_networkError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        networkError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.fetchCartItemsAndNotify(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onFetchCartItemsFailed();
        verify(mListenerMock2).onFetchCartItemsFailed();
    }

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactItemsSchemes());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private void generalError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(anyString(), any(Callback.class));
    }

    private List<Contact> getContact() {
        List<Contact> contact = new ArrayList<>();
        contact.add(new Contact(ID, FULL_NAME, IMAGE_URL));
        return contact;
    }

    private List<ContactSchema> getContactItemsSchemes() {
        List<ContactSchema> schemas = new ArrayList<>();
        schemas.add(new ContactSchema(ID, FULL_NAME, FULL_PHONE_NUMBER, IMAGE_URL, AGE));
        return schemas;
    }

}
