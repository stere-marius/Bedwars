package ro.marius.bedwars.utils.conversational;

public interface PromptFailureCallback<T> {


    void onFailure(T answer);

}
