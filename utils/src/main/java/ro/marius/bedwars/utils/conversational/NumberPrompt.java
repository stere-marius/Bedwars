package ro.marius.bedwars.utils.conversational;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.NumericPrompt;
import org.bukkit.conversations.Prompt;
import ro.marius.bedwars.utils.Utils;

import java.util.function.Predicate;

public class NumberPrompt extends NumericPrompt {

    private final String promptText;
    private final Predicate<Integer> integerPredicate;
    private final PromptSuccessCallback<Integer> promptSuccessCallback;
    private final PromptFailureCallback<Integer> promptFailureCallback;

    public NumberPrompt(String promptText,
                        Predicate<Integer> integerPredicate,
                        PromptSuccessCallback<Integer> promptSuccessCallback,
                        PromptFailureCallback<Integer> promptFailureCallback) {
        this.promptText = promptText;
        this.integerPredicate = integerPredicate;
        this.promptSuccessCallback = promptSuccessCallback;
        this.promptFailureCallback = promptFailureCallback;
    }


    @Override
    protected Prompt acceptValidatedInput(ConversationContext conversationContext, Number number) {

        if (integerPredicate.test(number.intValue())) {
            promptFailureCallback.onFailure(number.intValue());
            return Prompt.END_OF_CONVERSATION;
        }

        promptSuccessCallback.onSuccess(number.intValue());

        return Prompt.END_OF_CONVERSATION;
    }

    @Override
    public String getPromptText(ConversationContext conversationContext) {

        return Utils.translate(promptText);
    }
}
