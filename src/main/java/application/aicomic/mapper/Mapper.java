package application.aicomic.mapper;

import application.aicomic.enums.TransactionsEnums;
import org.mapstruct.MappingTarget;

import application.aicomic.dataAccess.ComicsDTO;
import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.CurrentChapterDTO;
import application.aicomic.dataAccess.GenresDTO;
import application.aicomic.dataAccess.OrderDetailsDTO;
import application.aicomic.dataAccess.OrdersDTO;
import application.aicomic.dataAccess.TransactionsDTO;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.models.Comics;
import application.aicomic.models.Comments;
import application.aicomic.models.CurrentChapter;
import application.aicomic.models.Genres;
import application.aicomic.models.OrderDetails;
import application.aicomic.models.Orders;
import application.aicomic.models.Transactions;
import application.aicomic.models.Wallets;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.enums.PurchasedCoinsEnums;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    CurrentChapter toCurrentChapter(CurrentChapterDTO currentChapterDTO);

    void updateCurrentChapter(@MappingTarget CurrentChapter currentChapters, CurrentChapterDTO currentChapterDTO);

    Comments toComments(CommentsDTO commentsDTO);

    void updateComments(@MappingTarget Comments comments, CommentsDTO commentsDTO);

    OrderDetails toOrderDetails(OrderDetailsDTO orderDetailsDTO);

    void updateOrderDetails(@MappingTarget OrderDetails orderDetails, OrderDetailsDTO orderDetailsDTO);

    Comics toComics(ComicsDTO comicsDTO);

    void updateComics(@MappingTarget Comics comics, ComicsDTO comicsDTO);

    void updateOrders(@MappingTarget Orders orders, OrdersDTO ordersDTO);

    void updateWallets(@MappingTarget Wallets wallets, WalletsDTO owalletsDTO);
    
    Genres toGenres(GenresDTO genresDTO);

    void updateGenres(@MappingTarget Genres genres, GenresDTO genresDTO);

    // Custom method to map Type enum to byte
    default byte mapType(TransactionsEnums.Type type) {
        return type == null ? 0 : type.getValue();
    }

    // Custom method to map byte to Type enum
    default TransactionsEnums.Type mapType(byte type) {
        return TransactionsEnums.Type.fromValue(type);
    }

    // Custom method to map Status enum to byte
    default byte mapStatus(TransactionsEnums type) {
        return type == null ? 0 : type.getValue();
    }

    // Custom method to map byte to Status enum
    default TransactionsEnums mapStatus(byte type) {
        return TransactionsEnums.fromValue(type);
    }

    // Update TransactionsEnums from DTO
    void updateTransactions(@MappingTarget Transactions transactions, TransactionsDTO transactionsDTO);


    PurchasedCoins toPurchasedCoins(PurchasedCoinsDTO purchasedCoinsDTO);

    void updatePurchasedCoins(@MappingTarget PurchasedCoins purchasedCoins, PurchasedCoinsDTO purchasedCoinsDTO);
}
