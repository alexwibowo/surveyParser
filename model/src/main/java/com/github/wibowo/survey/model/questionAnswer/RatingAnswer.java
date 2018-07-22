package com.github.wibowo.survey.model.questionAnswer;

public final class RatingAnswer extends BaseAnswer<RatingQuestion> {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;
    public static final int NULL_RATING = Integer.MIN_VALUE;

    private final int rating;
    private final boolean isNull;

    private RatingAnswer(final RatingQuestion question,
                        final int rating,
                         final boolean isNull) {
        super(question);
        this.rating = rating;
        this.isNull = isNull;
    }

    public static RatingAnswer createAnswer(final RatingQuestion question,
                                            final int rating) {
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new IllegalArgumentException(String.format("%d is an invalid rating value. Rating must be between %d and %d.", rating, MIN_RATING, MAX_RATING));
        }
        return new RatingAnswer(question, rating, false);
    }

    public static RatingAnswer nullAnswer(final RatingQuestion question) {
        return new RatingAnswer(question, NULL_RATING, true);
    }

    public boolean isNull() {
        return isNull;
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
