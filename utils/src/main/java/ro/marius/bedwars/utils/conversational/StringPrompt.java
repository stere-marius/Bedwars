package ro.marius.bedwars.utils.conversational;

import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import ro.marius.bedwars.utils.Utils;

import java.util.function.Predicate;

public class StringPrompt extends org.bukkit.conversations.StringPrompt {


    private final String promptText;
    private final PromptSuccessCallback<String> successCallback;
    private final PromptFailureCallback<String> failureCallback;
    private final Predicate<String> predicate;


    public StringPrompt(String promptText,
                        Predicate<String> predicate,
                        PromptSuccessCallback<String> successCallback,
                        PromptFailureCallback<String> failureCallback) {
        this.promptText = promptText;
        this.successCallback = successCallback;
        this.failureCallback = failureCallback;
        this.predicate = predicate;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {
        return Utils.translate(promptText);
    }

    @Override
    public Prompt acceptInput(ConversationContext conversationContext, String answer) {

        Conversable conversable = conversationContext.getForWhom();

        if (answer.isEmpty()) {
            conversable.sendRawMessage(Utils.translate("&eThe answer must not be empty."));
            return Prompt.END_OF_CONVERSATION;
        }

        if (predicate.test(answer)) {
            failureCallback.onFailure(answer);
            return Prompt.END_OF_CONVERSATION;
        }


        successCallback.onSuccess(answer);

        return Prompt.END_OF_CONVERSATION;
    }
}
