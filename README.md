# Kraken API tets

1. Install IntelliJ
2. in the terminal execute the following:
    a. chmod +x ./gradlew
    b. ./gradlew :test ${{ env.tests }}
        i.e --tests "com.kraken.test.KrakenAPITests"
