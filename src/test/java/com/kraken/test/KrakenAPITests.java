package com.kraken.test;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import utils.AbstractTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static utils.UrlMapping.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class KrakenAPITests extends AbstractTest {

	private static String tradeId1 = "XXBTZUSD";
	private static String tradeId2 = "XETHXXBT";

	@Test
	public void shouldFetchServerTime() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(TIME_API_URL);

		//then
		response.then().statusCode(200);
		Map<String, Object> systemTime = response.body().jsonPath().getMap("");
		assertThat(systemTime.size(), is(2));

		ArrayList error = (ArrayList) systemTime.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) systemTime.get("result");
		assertThat(result.size(), is(2));
		assertThat(result.get("status"), is("online"));
		assertThat(result.get("timestamp").toString(), containsString("2022"));
	}

	@Test
	public void shouldFetchSystemStatus() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(SYSTEM_STATUS_API_URL);

		//then
		response.then().statusCode(200);
		Map<String, Object> systemStatus = response.body().jsonPath().getMap("");
		assertThat(systemStatus.size(), is(2));

		ArrayList error = (ArrayList) systemStatus.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) systemStatus.get("result");
		assertThat(result.size(), is(2));
		assertThat(result.get("status"), is("online"));
		assertThat(result.get("timestamp").toString(), containsString("2022"));
	}

	@Test
	public void shouldNotFetchSystemStatus() {
		String invalidSystemStatus = "/0/private/SystemStatus";
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(KRAKEN_DOMAIN_URL + invalidSystemStatus);

		//then
		response.then().statusCode(200);
		Map<String, Object> systemStatus = response.body().jsonPath().getMap("");
		assertThat(systemStatus.size(), is(1));

		ArrayList error = (ArrayList) systemStatus.get("error");
		assertThat(error.size(), is(1));
		assertThat(error.get(0), is("EGeneral:Unknown method"));
	}

	@Test
	public void shouldNotFetchInvalidBooks() {
		String invalidBook = "";
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(BOOKS_API_URL + invalidBook);

		//then
		response.then().statusCode(200);
		Map<String, Object> booksResult = response.body().jsonPath().getMap("");
		assertThat(booksResult.size(), is(1));

		ArrayList error = (ArrayList) booksResult.get("error");
		assertThat(error.size(), is(1));
		assertThat(error.get(0), is("EQuery:Unknown asset pair"));
	}

	@Test
	public void shouldNotFetchInvalidBooksUrl() {
		String invalidBook = "";
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(KRAKEN_DOMAIN_URL + "/1/public/Depth?pair=" + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> booksResult = response.body().jsonPath().getMap("");
		assertThat(booksResult.size(), is(1));

		ArrayList error = (ArrayList) booksResult.get("error");
		assertThat(error.size(), is(1));
		assertThat(error.get(0), is("EGeneral:Unknown method"));
	}

	@Test
	public void shouldFetchBooks() {
		//given
		RequestSpecification requestSpecification = given().
                header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(BOOKS_API_URL + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> booksResult = response.body().jsonPath().getMap("");
		assertThat(booksResult.size(), is(2));

		ArrayList error = (ArrayList) booksResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) booksResult.get("result");
		assertThat(result.size(), is(1));

		HashMap book = (HashMap) result.get("XXBTZUSD");
		assertThat(book.size(), is(2));

		ArrayList asks = (ArrayList) book.get("asks");
		assertThat(asks.size(), is(100));
		ArrayList asksData = (ArrayList) asks.get(0);
		assertThat(asksData.get(0), notNullValue());
		assertThat(asksData.get(1), notNullValue());
		assertThat(asksData.get(2), notNullValue());

		ArrayList bids = (ArrayList) book.get("bids");
		assertThat(bids.size(), is(100));
		ArrayList bidsData = (ArrayList) bids.get(0);
		assertThat(bidsData.get(0), notNullValue());;
		assertThat(bidsData.get(1), notNullValue());
		assertThat(bidsData.get(2), notNullValue());
	}

	@Test
	public void shouldFetchBooksUpdate() throws InterruptedException {
		HashMap book = getBook();

		for(int counter = 0; counter < 10; counter++) {
			ArrayList asks = (ArrayList) book.get("asks");
			ArrayList asksData = (ArrayList) asks.get(0);

			String asksData1 = String.valueOf(asksData.get(0));
			String asksData2 = String.valueOf(asksData.get(1));
			String asksData3 = String.valueOf(asksData.get(2));

			ArrayList bids = (ArrayList) book.get("bids");
			ArrayList bidsData = (ArrayList) bids.get(0);

			String bidsData1 = String.valueOf(bidsData.get(0));
			String bidsData2 = String.valueOf(bidsData.get(1));
			String bidsData3 = String.valueOf(bidsData.get(2));
			Thread.sleep(5000);

			HashMap bookUpdated = getBook();

			ArrayList asksUpdated = (ArrayList) bookUpdated.get("asks");
			ArrayList asksDataUpdated = (ArrayList) asksUpdated.get(0);

			String asksData1Updated = String.valueOf(asksDataUpdated.get(0));
			String asksData2Updated = String.valueOf(asksDataUpdated.get(1));
			String asksData3Updated = String.valueOf(asksDataUpdated.get(2));

			ArrayList bidsUpdated = (ArrayList) bookUpdated.get("bids");
			ArrayList bidsDataUpdated = (ArrayList) bidsUpdated.get(0);

			String bidsData1Updated = String.valueOf(bidsDataUpdated.get(0));
			String bidsData2Updated = String.valueOf(bidsDataUpdated.get(1));
			String bidsData3Updated = String.valueOf(bidsDataUpdated.get(2));

			assertThat(asksData1, not(asksData1Updated));
			assertThat(asksData2, not(asksData2Updated));
			assertThat(asksData3, not(asksData3Updated));
			assertThat(bidsData1, not(bidsData1Updated));
			assertThat(bidsData2, not(bidsData2Updated));
			assertThat(bidsData3, not(bidsData3Updated));
		}
	}

	private HashMap getBook() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(BOOKS_API_URL + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> booksResult = response.body().jsonPath().getMap("");
		assertThat(booksResult.size(), is(2));

		HashMap result = (HashMap) booksResult.get("result");
		assertThat(result.size(), is(1));

		HashMap book = (HashMap) result.get("XXBTZUSD");
		assertThat(book.size(), is(2));
		return book;
	}

	@Test
	public void shouldFetchOHCL() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(OHLC_API_URL + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> ochlResult = response.body().jsonPath().getMap("");
		assertThat(ochlResult.size(), is(2));

		ArrayList error = (ArrayList) ochlResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) ochlResult.get("result");
		assertThat(result.size(), is(2));

		Integer last = (Integer) result.get("last");
		assertThat(last, notNullValue());

		ArrayList OHCL = (ArrayList) result.get("XXBTZUSD");
		assertThat(OHCL.size(), is(720));

		for (int counter = 0; counter < OHCL.size(); counter++) {
			ArrayList OHCLData = (ArrayList) OHCL.get(counter);
			assertThat(OHCLData.get(0), notNullValue());
			assertThat(OHCLData.get(1), notNullValue());
			assertThat(OHCLData.get(2), notNullValue());

		}
	}

	@Test
	public void shouldFetchSpread() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(SPREAD_API_URL + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> spreadResult = response.body().jsonPath().getMap("");
		assertThat(spreadResult.size(), is(2));

		ArrayList error = (ArrayList) spreadResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) spreadResult.get("result");
		assertThat(result.size(), is(2));

		Integer last = (Integer) result.get("last");
		assertThat(last, notNullValue());

		ArrayList spread = (ArrayList) result.get("XXBTZUSD");
		assertTrue(spread.size() > 1);

		for (int counter = 0; counter < spread.size(); counter++) {
			ArrayList spreadData = (ArrayList) spread.get(counter);
			assertThat(spreadData.get(0), notNullValue());
			assertThat(spreadData.get(1), notNullValue());
			assertThat(spreadData.get(2), notNullValue());
		}
	}

	@Test
	public void shouldFetchTicker() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(TICKER_API_URL + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> tickerResult = response.body().jsonPath().getMap("");
		assertThat(tickerResult.size(), is(2));

		ArrayList error = (ArrayList) tickerResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) tickerResult.get("result");
		assertThat(result.size(), is(1));

		HashMap ticker = (HashMap) result.get("XXBTZUSD");
		assertThat(ticker.size(), is(9));

		ArrayList tickerPData = (ArrayList) ticker.get("p");
		assertThat(tickerPData.size(), is(2));
		assertThat(tickerPData.get(0), notNullValue());
		assertThat(tickerPData.get(1), notNullValue());

		ArrayList tickerAData = (ArrayList) ticker.get("a");
		assertThat(tickerAData.size(), is(3));
		assertThat(tickerAData.get(0), notNullValue());
		assertThat(tickerAData.get(1), notNullValue());
		assertThat(tickerAData.get(2), notNullValue());

		ArrayList tickerBData = (ArrayList) ticker.get("b");
		assertThat(tickerBData.size(), is(3));
		assertThat(tickerBData.get(0), notNullValue());
		assertThat(tickerBData.get(1), notNullValue());
		assertThat(tickerBData.get(2), notNullValue());

		ArrayList tickerCData = (ArrayList) ticker.get("c");
		assertThat(tickerCData.size(), is(2));
		assertThat(tickerCData.get(0), notNullValue());
		assertThat(tickerCData.get(1), notNullValue());

		ArrayList tickerTData = (ArrayList) ticker.get("t");
		assertThat(tickerTData.size(), is(2));
		assertThat(tickerTData.get(0), notNullValue());
		assertThat(tickerTData.get(1), notNullValue());

		ArrayList tickerVData = (ArrayList) ticker.get("v");
		assertThat(tickerVData.size(), is(2));
		assertThat(tickerVData.get(0), notNullValue());
		assertThat(tickerVData.get(1), notNullValue());

		ArrayList tickerHData = (ArrayList) ticker.get("h");
		assertThat(tickerHData.size(), is(2));
		assertThat(tickerHData.get(0), notNullValue());
		assertThat(tickerHData.get(1), notNullValue());

		ArrayList tickerLData = (ArrayList) ticker.get("l");
		assertThat(tickerLData.size(), is(2));
		assertThat(tickerLData.get(0), notNullValue());
		assertThat(tickerLData.get(1), notNullValue());

		String tickerOData = (String) ticker.get("o");
		assertThat(tickerOData, notNullValue());
	}

	@Test
	public void shouldFetchAllTrades() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(ALL_TRADES_API_URL);

		//then
		response.then().statusCode(200);
		Map<String, Object> tradeResult = response.body().jsonPath().getMap("");
		assertThat(tradeResult.size(), is(2));

		ArrayList error = (ArrayList) tradeResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) tradeResult.get("result");
		assertThat(result.size(), is(601));
	}

	@Test
	public void shouldNotFetchNotExistingTrade() {
		String notFoundTrade = "0000Z!£^^^))&^^";
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(TRADE_API_URL + notFoundTrade);

		//then
		response.then().statusCode(200);
		Map<String, Object> tradeResult = response.body().jsonPath().getMap("");
		assertThat(tradeResult.size(), is(1));

		ArrayList error = (ArrayList) tradeResult.get("error");
		assertThat(error.size(), is(1));
		assertThat(error.get(0), is("EQuery:Unknown asset pair"));
	}

	@Test
	public void shouldFetchTrade() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(TRADE_API_URL + tradeId1);

		//then
		response.then().statusCode(200);
		Map<String, Object> tradeResult = response.body().jsonPath().getMap("");
		assertThat(tradeResult.size(), is(2));

		ArrayList error = (ArrayList) tradeResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) tradeResult.get("result");
		assertThat(result.size(), is(1));

		HashMap tradeXXBTZUSD = (HashMap) result.get("XXBTZUSD");
		assertThat(tradeXXBTZUSD.size(), is(18));

		ArrayList tradeFees = (ArrayList) tradeXXBTZUSD.get("fees");
		assertThat(tradeFees.size(), is(9));
		for (int counter = 0; counter < tradeFees.size(); counter++) {
			ArrayList tradeFeesData = (ArrayList) tradeFees.get(counter);
			assertThat(tradeFeesData.get(0), is(notNullValue()));
			assertThat(tradeFeesData.get(1), is(notNullValue()));
		}

		ArrayList tradeFeesMaker = (ArrayList) tradeXXBTZUSD.get("fees_maker");
		assertThat(tradeFeesMaker.size(), is(9));
		for (int counter = 0; counter < tradeFeesMaker.size(); counter++) {
			ArrayList tradeFeesMakerData = (ArrayList) tradeFeesMaker.get(counter);
			assertThat(tradeFeesMakerData.get(0), is(notNullValue()));
			assertThat(tradeFeesMakerData.get(1), is(notNullValue()));
		}

		ArrayList tradeLeverageSell = (ArrayList) tradeXXBTZUSD.get("leverage_sell");
		assertThat(tradeLeverageSell.size(), is(4));
		for (int counter = 0; counter < tradeLeverageSell.size(); counter++) {
			ArrayList tradeLeverageSellData = (ArrayList) tradeFeesMaker.get(counter);
			assertThat(tradeLeverageSellData.get(0), is(notNullValue()));
		}

		assertThat(tradeXXBTZUSD.get("aclass_base"), is("currency"));
		assertThat(tradeXXBTZUSD.get("altname"), is("XBTUSD"));
		assertThat(tradeXXBTZUSD.get("margin_stop"), is(40));
		assertThat(tradeXXBTZUSD.get("lot_multiplier"), is(1));
		assertThat(tradeXXBTZUSD.get("lot_decimals"), is(8));
		assertThat(tradeXXBTZUSD.get("lot"), is("unit"));
		assertThat(tradeXXBTZUSD.get("quote"), is("ZUSD"));
		assertThat(tradeXXBTZUSD.get("ordermin"), is("0.0001"));
		assertThat(tradeXXBTZUSD.get("pair_decimals"), is(1));

		ArrayList tradeLeverageBuy = (ArrayList) tradeXXBTZUSD.get("leverage_buy");
		assertThat(tradeLeverageBuy.size(), is(4));
		for (int counter = 0; counter < tradeLeverageBuy.size(); counter++) {
			Integer tradeLeverageBuyData = (Integer) tradeLeverageBuy.get(counter);
			assertThat(tradeLeverageBuyData, is(notNullValue()));
		}

		assertThat(tradeXXBTZUSD.get("fee_volume_currency"), is("ZUSD"));
		assertThat(tradeXXBTZUSD.get("margin_call"), is(80));
		assertThat(tradeXXBTZUSD.get("aclass_quote"), is("currency"));
		assertThat(tradeXXBTZUSD.get("wsname"), is("XBT/USD"));
		assertThat(tradeXXBTZUSD.get("base"), is("XXBT"));
	}

	@Test
	public void shouldFetchMultipleTrades() {
		//given
		RequestSpecification requestSpecification = given().
				header("Content-Type", "application/x-www-form-urlencoded");
		//when
		Response response = requestSpecification.
				given().
				when().
				get(TRADE_API_URL + tradeId1 + "," + tradeId2);

		//then
		response.then().statusCode(200);
		Map<String, Object> tradeResult = response.body().jsonPath().getMap("");
		assertThat(tradeResult.size(), is(2));

		ArrayList error = (ArrayList) tradeResult.get("error");
		assertThat(error.size(), is(0));

		HashMap result = (HashMap) tradeResult.get("result");
		assertThat(result.size(), is(2));

		HashMap tradeXETHXXBT = (HashMap) result.get("XETHXXBT");
		assertThat(tradeXETHXXBT.size(), is(18));

		ArrayList tradeFees = (ArrayList) tradeXETHXXBT.get("fees");
		assertThat(tradeFees.size(), is(9));
		for (int counter = 0; counter < tradeFees.size(); counter++) {
			ArrayList tradeFeesData = (ArrayList) tradeFees.get(counter);
			assertThat(tradeFeesData.get(0), is(notNullValue()));
			assertThat(tradeFeesData.get(1), is(notNullValue()));
		}

		ArrayList tradeFeesMaker = (ArrayList) tradeXETHXXBT.get("fees_maker");
		assertThat(tradeFeesMaker.size(), is(9));
		for (int counter = 0; counter < tradeFeesMaker.size(); counter++) {
			ArrayList tradeFeesMakerData = (ArrayList) tradeFeesMaker.get(counter);
			assertThat(tradeFeesMakerData.get(0), is(notNullValue()));
			assertThat(tradeFeesMakerData.get(1), is(notNullValue()));
		}

		ArrayList tradeLeverageSell = (ArrayList) tradeXETHXXBT.get("leverage_sell");
		assertThat(tradeLeverageSell.size(), is(4));
		for (int counter = 0; counter < tradeLeverageSell.size(); counter++) {
			ArrayList tradeLeverageSellData = (ArrayList) tradeFeesMaker.get(counter);
			assertThat(tradeLeverageSellData.get(0), is(notNullValue()));
		}

		assertThat(tradeXETHXXBT.get("aclass_base"), is("currency"));
		assertThat(tradeXETHXXBT.get("altname"), is("ETHXBT"));
		assertThat(tradeXETHXXBT.get("margin_stop"), is(40));
		assertThat(tradeXETHXXBT.get("lot_multiplier"), is(1));
		assertThat(tradeXETHXXBT.get("lot_decimals"), is(8));
		assertThat(tradeXETHXXBT.get("lot"), is("unit"));
		assertThat(tradeXETHXXBT.get("quote"), is("XXBT"));
		assertThat(tradeXETHXXBT.get("ordermin"), is("0.01"));
		assertThat(tradeXETHXXBT.get("pair_decimals"), is(5));

		ArrayList tradeLeverageBuy = (ArrayList) tradeXETHXXBT.get("leverage_buy");
		assertThat(tradeLeverageBuy.size(), is(4));
		for (int counter = 0; counter < tradeLeverageBuy.size(); counter++) {
			Integer tradeLeverageBuyData = (Integer) tradeLeverageBuy.get(counter);
			assertThat(tradeLeverageBuyData, is(notNullValue()));
		}

		assertThat(tradeXETHXXBT.get("fee_volume_currency"), is("ZUSD"));
		assertThat(tradeXETHXXBT.get("margin_call"), is(80));
		assertThat(tradeXETHXXBT.get("aclass_quote"), is("currency"));
		assertThat(tradeXETHXXBT.get("wsname"), is("ETH/XBT"));
		assertThat(tradeXETHXXBT.get("base"), is("XETH"));
	}

}