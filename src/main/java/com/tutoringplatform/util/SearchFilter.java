package com.tutoringplatform.util;

import java.util.HashMap;
import java.util.Map;

public class SearchFilter {
    private Map<String, Object> filters;

    public SearchFilter() {
        this.filters = new HashMap<>();
    }

    public void addFilter(String key, Object value) {
        filters.put(key, value);
    }

    public void removeFilter(String key) {
        filters.remove(key);
    }

    public Object getFilter(String key) {
        return filters.get(key);
    }

    public boolean hasFilter(String key) {
        return filters.containsKey(key);
    }

    public Map<String, Object> getAllFilters() {
        return new HashMap<>(filters);
    }

    public void clear() {
        filters.clear();
    }

    public static final String SUBJECT = "subject";
    public static final String MIN_PRICE = "minPrice";
    public static final String MAX_PRICE = "maxPrice";
    public static final String MIN_RATING = "minRating";
    public static final String AVAILABILITY_DAY = "availabilityDay";
    public static final String AVAILABILITY_HOUR = "availabilityHour";
    public static final String CATEGORY = "category";
}