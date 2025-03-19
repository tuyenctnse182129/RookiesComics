package application.aicomic.mapper;

import application.aicomic.dataAccess.*;
import application.aicomic.enums.TransactionsEnums;
import application.aicomic.models.*;
import org.mapstruct.MappingTarget;

import application.aicomic.enums.PurchasedCoinsEnums;

@org.mapstruct.Mapper(componentModel = "spring")
public interface Mapper {
    Chapters toChapters(ChaptersDTO chaptersDTO);

    void updateChapters(@MappingTarget Chapters chapters, ChaptersDTO chaptersDTO);
    
    CurrentChapter toCurrentChapter(CurrentChapterDTO currentChapterDTO);

    void updateCurrentChapter(@MappingTarget CurrentChapter currentChapters, CurrentChapterDTO currentChapterDTO);

    Comments toComments(CommentsDTO commentsDTO);

    void updateComments(@MappingTarget Comments comments, CommentsDTO commentsDTO);

    Users toUsers(UsersDTO usersDTO);

    void updateUsers(@MappingTarget Users users, UsersDTO usersDTO);

    OrderDetails toOrderDetails(OrderDetailsDTO orderDetailsDTO);

    void updateOrderDetails(@MappingTarget OrderDetails orderDetails, OrderDetailsDTO orderDetailsDTO);

    Comics toComics(ComicsDTO comicsDTO);

    void updateComics(@MappingTarget Comics comics, ComicsDTO comicsDTO);

    void updateOrders(@MappingTarget Orders orders, OrdersDTO ordersDTO);

    void updateWallets(@MappingTarget Wallets wallets, WalletsDTO owalletsDTO);
    
    Genres toGenres(GenresDTO genresDTO);

    void updateGenres(@MappingTarget Genres genres, GenresDTO genresDTO);
    
    Bookshelves toBookshelves(BookshelvesDTO bookshelvesDTO);

    void updateBookshelves(@MappingTarget Bookshelves bookshelves, BookshelvesDTO bookshelvesDTO);

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
