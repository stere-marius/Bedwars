package ro.marius.bedwars.utils.conversational;

@FunctionalInterface
public interface PromptSuccessCallback<T> {


    void onSuccess(T answer);

}
