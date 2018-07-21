package com.github.wibowo.survey.model.questionAnswer;

public final class RatingAnswer extends BaseAnswer<RatingQuestion> {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final int rating;

    public RatingAnswer(final RatingQuestion question,
                        final int rating) {
        super(question);
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new IllegalArgumentException(String.format("Rating must be between %d and %d.", MIN_RATING, MAX_RATING));
        }
        this.rating = rating;
    }

    public int rating() {
        return rating;
    }


    @Override
    public String toString() {
        return "RatingAnswer{" +
                "question=" + question +
                ", rating=" + rating +
                '}';
    }
}
