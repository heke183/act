package com.xianglin.core.service.filter;

import com.xianglin.core.model.base.ActivityRequest;
import com.xianglin.core.model.base.ActivityResponse;

import java.util.ArrayList;
import java.util.List;


/**
 * @see org.springframework.web.filter.CompositeFilter
 *
 * @author yefei
 * @date 2018 -1-25 11:03:43
 */
public class CompositeFilter implements Filter {

    private List<? extends Filter> filters = new ArrayList<>();

    public void setFilters(List<? extends Filter> filters) {
        this.filters = new ArrayList<>(filters);
    }

    @Override
    public void doFilter(ActivityRequest<?> request, ActivityResponse response, FilterChain filterChain) {
        new VirtualFilterChain(filterChain, this.filters).doFilter(request, response);
    }


    private static class VirtualFilterChain implements FilterChain {

        private final FilterChain originalChain;

        private final List<? extends Filter> additionalFilters;

        private int currentPosition = 0;

        /**
         * Instantiates a new Virtual filter chain.
         *
         * @param chain             the chain
         * @param additionalFilters the additional filters
         */
        public VirtualFilterChain(FilterChain chain, List<? extends Filter> additionalFilters) {
            this.originalChain = chain;
            this.additionalFilters = additionalFilters;
        }

        @Override
        public void doFilter(ActivityRequest<?> request, ActivityResponse response) {

            if (this.currentPosition == this.additionalFilters.size()) {
                this.originalChain.doFilter(request, response);
            } else {
                this.currentPosition++;
                Filter nextFilter = this.additionalFilters.get(this.currentPosition - 1);
                nextFilter.doFilter(request, response, this);
            }
        }
    }

}
