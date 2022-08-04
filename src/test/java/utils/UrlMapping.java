package utils;

public class UrlMapping {

    public static final String KRAKEN_DOMAIN_URL = "https://api.kraken.com";

    public static final String BOOKS_API_URL = KRAKEN_DOMAIN_URL + "/0/public/Depth?pair=";

    public static final String OHLC_API_URL = KRAKEN_DOMAIN_URL + "/0/public/OHLC?pair=";

    public static final String SPREAD_API_URL = KRAKEN_DOMAIN_URL + "/0/public/Spread?pair=";

    public static final String TICKER_API_URL = KRAKEN_DOMAIN_URL + "/0/public/Ticker?pair=";

    public static final String TRADE_API_URL = KRAKEN_DOMAIN_URL + "/0/public/AssetPairs?pair=";

    public static final String ALL_TRADES_API_URL = KRAKEN_DOMAIN_URL + "/0/public/AssetPairs";

    public static final String SYSTEM_STATUS_API_URL = KRAKEN_DOMAIN_URL + "/0/public/SystemStatus";

    public static final String TIME_API_URL = KRAKEN_DOMAIN_URL + "/0/public/SystemStatus";

}