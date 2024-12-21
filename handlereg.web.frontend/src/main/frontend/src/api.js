import { createApi, fetchBaseQuery } from '@reduxjs/toolkit/query/react';

export const api = createApi({
    reducerPath: 'api',
    baseQuery: (...args) => {
        const api = args[1];
        const basename = api.getState().basename;
        return fetchBaseQuery({ baseUrl: basename + '/api' })(...args);
    },
    endpoints: (builder) => ({
        getLogintilstand: builder.query({ query: () => '/logintilstand' }),
        getOversikt: builder.query({ query: () => '/oversikt' }),
        getHandlinger: builder.query({ query: (accountId) => '/handlinger/' + accountId.toString(), providesTags: ['Handlinger'] }),
        getButikker: builder.query({ query: () => '/butikker', providesTags: ['Butikker'] }),
        getFavoritter: builder.query({ query: (username) => '/favoritter?username=' + username, providesTags: ['Favoritter'] }),
        getSumButikk: builder.query({ query: () => '/statistikk/sumbutikk', providesTags: ['Handlinger'] }),
        getHandlingerButikk: builder.query({ query: () => '/statistikk/handlingerbutikk', providesTags: ['Handlinger'] }),
        getSisteHandel: builder.query({ query: () => '/statistikk/sistehandel', providesTags: ['Handlinger'] }),
        getSumYear: builder.query({ query: () => '/statistikk/sumyear', providesTags: ['Handlinger'] }),
        getSumYearMonth: builder.query({ query: () => '/statistikk/sumyearmonth', providesTags: ['Handlinger'] }),
        postLogin: builder.mutation({ query: (body) => ({url: '/login', method: 'POST', body }) }),
        getLogout: builder.mutation({ query: () => ({url: '/logout', method: 'GET' }) }),
        postNyhandling: builder.mutation({
            query: (body) => ({url: '/nyhandling', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: postNyhandlingResult } = await queryFulfilled;
                    dispatch(api.util.upsertQueryEntries('getOversikt', body, (draft) => Object.assign(draft, postNyhandlingResult)));
                } catch {}
            },
            invalidatesTags: ['Handlinger'],
        }),
        postEndrebutikk: builder.mutation({
            query: (body) => ({ url: '/endrebutikk', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: postEndrebutikkResult } = await queryFulfilled;
                    console.log(dispatch);
                    const updateResult = dispatch(api.util.updateQueryData('getButikker', undefined, (draft) => Object.assign(draft, postEndrebutikkResult)));
                } catch {}
            },
        }),
        postNybutikk: builder.mutation({
            query: (body) => ({ url: '/nybutikk', method: 'POST', body }),
            invalidatesTags: ['Butikker'],
        }),
        postFavorittLeggtil: builder.mutation({
            query: (body) => ({ url: '/favoritt/leggtil', method: 'POST', body }),
            invalidatesTags: ['Favoritter'],
        }),
        postFavorittSlett: builder.mutation({
            query: (body) => ({ url: '/favoritt/slett', method: 'POST', body }),
            invalidatesTags: ['Favoritter'],
        }),
        postFavorittBytt: builder.mutation({
            query: (body) => ({ url: '/favoritter/bytt', method: 'POST', body }),
            invalidatesTags: ['Favoritter'],
        }),
    }),
});

export const {
    useGetLogintilstandQuery,
    useGetOversiktQuery,
    useGetHandlingerQuery,
    useGetButikkerQuery,
    useGetFavoritterQuery,
    useGetSumButikkQuery,
    useGetHandlingerButikkQuery,
    useGetSisteHandelQuery,
    useGetSumYearQuery,
    useGetSumYearMonthQuery,
    usePostLoginMutation,
    useGetLogoutMutation,
    usePostNyhandlingMutation,
    usePostEndrebutikkMutation,
    usePostNybutikkMutation,
    usePostFavorittLeggtilMutation,
    usePostFavorittSlettMutation,
    usePostFavorittByttMutation,
} = api;
