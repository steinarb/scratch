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
        getOversikt: builder.query({
            query: () => '/oversikt',
            merge: (currentCache, newItems) => currentCache.push(...newItems),
        }),
        getHandlinger: builder.query({ query: (accountId) => '/handlinger/' + accountId.toString(), providesTags: ['Handlinger'] }),
        getButikker: builder.query({
            query: () => '/butikker',
            merge: (currentCache, newItems) => currentCache.push(...newItems),
        }),
        getFavoritter: builder.query({
            query: (username) => '/favoritter?username=' + username,
            merge: (currentCache, newItems) => currentCache.push(...newItems),
        }),
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
                    dispatch(api.util.upsertQueryEntries([ { endpointName: 'getOversikt', arg: undefined, value: [ postNyhandlingResult ] } ]));
                } catch {}
            },
            invalidatesTags: ['Handlinger'],
        }),
        postEndrebutikk: builder.mutation({
            query: (body) => ({ url: '/endrebutikk', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: postEndrebutikkResult } = await queryFulfilled;
                    dispatch(api.util.upsertQueryEntries([ { endpointName: 'getButikker', arg: undefined, value: postEndrebutikkResult } ]));
                } catch {}
            },
        }),
        postNybutikk: builder.mutation({
            query: (body) => ({ url: '/nybutikk', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: postNybutikkResult } = await queryFulfilled;
                    dispatch(api.util.upsertQueryEntries([ { endpointName: 'getButikker', arg: undefined, value: postNybutikkResult } ]));
                } catch {}
            },
        }),
        postFavorittLeggtil: builder.mutation({
            query: (body) => ({ url: '/favoritt/leggtil', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: postFavorittLeggtilResult } = await queryFulfilled;
                    dispatch(api.util.upsertQueryEntries([ { endpointName: 'getFavoritter', arg: body.brukernavn, value: postFavorittLeggtilResult } ]));
                } catch {}
            },
        }),
        postFavorittSlett: builder.mutation({
            query: (body) => ({ url: '/favoritt/slett', method: 'POST', body }),
            async onQueryStarted(body, { dispatch, queryFulfilled }) {
                try {
                    const { data: postFavorittSlettResult } = await queryFulfilled;
                    dispatch(api.util.upsertQueryEntries([ { endpointName: 'getFavoritter', arg: body.brukernavn, value: postFavorittSlettResult } ]));
                } catch(e) {
                    console.log(e);
                }
            },
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
