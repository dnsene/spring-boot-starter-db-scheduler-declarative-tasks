package io.github.dnsene.dbscheduler.declarativetasks.boot.util;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Pair<S, T> {

	private final S first;
	private final T second;

	private Pair(S first, T second) {

		Assert.notNull(first, "First must not be null");
		Assert.notNull(second, "Second must not be null");

		this.first = first;
		this.second = second;
	}


	public static <S, T> Pair<S, T> of(S first, T second) {
		return new Pair<>(first, second);
	}

	public S getFirst() {
		return first;
	}

	public T getSecond() {
		return second;
	}

	public static <S, T> Collector<Pair<S, T>, ?, Map<S, T>> toMap() {
		return Collectors.toMap(Pair::getFirst, Pair::getSecond);
	}

	@Override
	public boolean equals(@Nullable Object o) {

		if (this == o) {
			return true;
		}

		if (!(o instanceof Pair<?, ?> )) {
			return false;
		}
		Pair<?, ?> pair = (Pair<?, ?>) o;

		if (!ObjectUtils.nullSafeEquals(first, pair.first)) {
			return false;
		}

		return ObjectUtils.nullSafeEquals(second, pair.second);
	}

	@Override
	public int hashCode() {
		int result = ObjectUtils.nullSafeHashCode(first);
		result = 31 * result + ObjectUtils.nullSafeHashCode(second);
		return result;
	}

	@Override
	public String toString() {
		return String.format("%s->%s", this.first, this.second);
	}
}
