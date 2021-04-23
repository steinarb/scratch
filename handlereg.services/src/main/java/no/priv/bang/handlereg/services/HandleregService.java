/*
 * Copyright 2019 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.handlereg.services;

import java.util.List;

public interface HandleregService {

    Oversikt finnOversikt(String brukernavn);

    List<Transaction> findLastTransactions(int accountId);

    Oversikt registrerHandling(NyHandling handling);

    List<Butikk> finnButikker();

    List<Butikk> leggTilButikk(Butikk nybutikk);

    List<Butikk> endreButikk(Butikk nybutikk);

    List<ButikkSum> sumOverButikk();

    List<ButikkCount> antallHandlingerIButikk();

    List<ButikkDate> sisteHandelIButikk();

    List<SumYear> totaltHandlebelopPrAar();

    List<SumYearMonth> totaltHandlebelopPrAarOgMaaned();

    List<Favoritt> finnFavoritter(String brukernavn);

    List<Favoritt> leggTilFavoritt(NyFavoritt nyFavoritt);

    List<Favoritt> slettFavoritt(Favoritt skalSlettes);

    List<Favoritt> byttRekkefolge(Favorittpar parSomSkalBytteRekkfolge);

}
