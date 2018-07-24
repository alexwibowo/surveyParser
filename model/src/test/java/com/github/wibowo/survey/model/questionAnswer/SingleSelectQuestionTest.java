package com.github.wibowo.survey.model.questionAnswer;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SingleSelectQuestionTest {

    @Test
    void test_create() {
        final SingleSelectQuestion question1 = new SingleSelectQuestion(0, Theme.Work, "Manager");
        final SingleSelectQuestion question2 = new SingleSelectQuestion(1, Theme.Demographic, "City");

        assertThat(question1.theme()).isEqualTo(Theme.Work);
        assertThat(question1.sentence()).isEqualTo("Manager");
        assertThat(question2.theme()).isEqualTo(Theme.Demographic);
        assertThat(question2.sentence()).isEqualTo("City");
    }

}