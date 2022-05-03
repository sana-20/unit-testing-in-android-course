package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactUseCase {

    public interface Listener {
        void onContactItemsFetched(List<Contact> contacts);
        void onFetchCartItemsFailed();
    }

    private final List<FetchContactUseCase.Listener> mListeners = new ArrayList<>();
    private final GetContactsHttpEndpoint mGetContactsHttpEndpoint;

    public FetchContactUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        mGetContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchCartItemsAndNotify(String filterTerm) {
        mGetContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> cartItems) {
                notifySucceeded(cartItems);
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason) {
                    case GENERAL_ERROR:
                    case NETWORK_ERROR:
                        notifyFailed();
                        break;
                }
            }
        });
    }

    private void notifyFailed() {
        for (Listener listener: mListeners) {
            listener.onFetchCartItemsFailed();
        }
    }

    private void notifySucceeded(List<ContactSchema> contactSchemas) {
        for (Listener listener : mListeners){
            listener.onContactItemsFetched(contactFromSchemas((contactSchemas)));
        }
    }

    private List<Contact> contactFromSchemas(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema: contactSchemas){
            contacts.add(new Contact(schema.getId(), schema.getFullName(), schema.getImageUrl()));
        }
        return contacts;
    }

    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(Listener listener){
        mListeners.remove(listener);
    }

}
