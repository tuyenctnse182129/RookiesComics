package application.aicomic.mapper;

import application.aicomic.dataAccess.ComicsDTO;
import application.aicomic.dataAccess.CommentsDTO;
import application.aicomic.dataAccess.CurrentChapterDTO;
import application.aicomic.dataAccess.GenresDTO;
import application.aicomic.dataAccess.OrderDetailsDTO;
import application.aicomic.dataAccess.OrdersDTO;
import application.aicomic.dataAccess.PurchasedCoinsDTO;
import application.aicomic.dataAccess.TransactionsDTO;
import application.aicomic.dataAccess.WalletsDTO;
import application.aicomic.models.Comics;
import application.aicomic.models.Comments;
import application.aicomic.models.CurrentChapter;
import application.aicomic.models.Genres;
import application.aicomic.models.OrderDetails;
import application.aicomic.models.Orders;
import application.aicomic.models.PurchasedCoins;
import application.aicomic.models.Transactions;
import application.aicomic.models.Wallets;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-02-22T19:40:04+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 23.0.1 (Oracle Corporation)"
)
@Component
public class MapperImpl implements Mapper {

    private final DatatypeFactory datatypeFactory;

    public MapperImpl() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch ( DatatypeConfigurationException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public CurrentChapter toCurrentChapter(CurrentChapterDTO currentChapterDTO) {
        if ( currentChapterDTO == null ) {
            return null;
        }

        CurrentChapter currentChapter = new CurrentChapter();

        currentChapter.setCurrentChaperId( currentChapterDTO.getCurrentChaperId() );
        currentChapter.setStatus( currentChapterDTO.getStatus() );
        currentChapter.setComicId( currentChapterDTO.getComicId() );
        currentChapter.setChapterId( currentChapterDTO.getChapterId() );
        currentChapter.setUserId( currentChapterDTO.getUserId() );

        return currentChapter;
    }

    @Override
    public void updateCurrentChapter(CurrentChapter currentChapters, CurrentChapterDTO currentChapterDTO) {
        if ( currentChapterDTO == null ) {
            return;
        }

        currentChapters.setCurrentChaperId( currentChapterDTO.getCurrentChaperId() );
        currentChapters.setStatus( currentChapterDTO.getStatus() );
        currentChapters.setComicId( currentChapterDTO.getComicId() );
        currentChapters.setChapterId( currentChapterDTO.getChapterId() );
        currentChapters.setUserId( currentChapterDTO.getUserId() );
    }

    @Override
    public Comments toComments(CommentsDTO commentsDTO) {
        if ( commentsDTO == null ) {
            return null;
        }

        Comments comments = new Comments();

        comments.setCommentId( commentsDTO.getCommentId() );
        comments.setContent( commentsDTO.getContent() );
        comments.setUserId( commentsDTO.getUserId() );
        comments.setChapterId( commentsDTO.getChapterId() );
        comments.setCreatedDate( commentsDTO.getCreatedDate() );
        comments.setStatus( commentsDTO.getStatus() );
        comments.setChapter( commentsDTO.getChapter() );

        return comments;
    }

    @Override
    public void updateComments(Comments comments, CommentsDTO commentsDTO) {
        if ( commentsDTO == null ) {
            return;
        }

        comments.setCommentId( commentsDTO.getCommentId() );
        comments.setContent( commentsDTO.getContent() );
        comments.setUserId( commentsDTO.getUserId() );
        comments.setChapterId( commentsDTO.getChapterId() );
        comments.setCreatedDate( commentsDTO.getCreatedDate() );
        comments.setStatus( commentsDTO.getStatus() );
        comments.setChapter( commentsDTO.getChapter() );
    }

    @Override
    public Transactions toTransactions(TransactionsDTO transactionsDTO) {
        if ( transactionsDTO == null ) {
            return null;
        }

        Transactions transactions = new Transactions();

        transactions.setTransactionId( transactionsDTO.getTransactionId() );
        transactions.setTransactionCode( transactionsDTO.getTransactionCode() );
        transactions.setContent( transactionsDTO.getContent() );
        transactions.setBankName( transactionsDTO.getBankName() );
        transactions.setTransactionTime( transactionsDTO.getTransactionTime() );
        transactions.setAmount( transactionsDTO.getAmount() );
        transactions.setStatus( transactionsDTO.getStatus() );
        transactions.setOrderId( transactionsDTO.getOrderId() );
        transactions.setWalletId( transactionsDTO.getWalletId() );
        transactions.setPurchasedCoinId( transactionsDTO.getPurchasedCoinId() );

        return transactions;
    }

    @Override
    public void updateTransactions(Transactions transactions, TransactionsDTO transactionsDTO) {
        if ( transactionsDTO == null ) {
            return;
        }

        transactions.setTransactionId( transactionsDTO.getTransactionId() );
        transactions.setTransactionCode( transactionsDTO.getTransactionCode() );
        transactions.setContent( transactionsDTO.getContent() );
        transactions.setBankName( transactionsDTO.getBankName() );
        transactions.setTransactionTime( transactionsDTO.getTransactionTime() );
        transactions.setAmount( transactionsDTO.getAmount() );
        transactions.setStatus( transactionsDTO.getStatus() );
        transactions.setOrderId( transactionsDTO.getOrderId() );
        transactions.setWalletId( transactionsDTO.getWalletId() );
        transactions.setPurchasedCoinId( transactionsDTO.getPurchasedCoinId() );
    }

    @Override
    public OrderDetails toOrderDetails(OrderDetailsDTO orderDetailsDTO) {
        if ( orderDetailsDTO == null ) {
            return null;
        }

        OrderDetails orderDetails = new OrderDetails();

        orderDetails.setOrderDetailId( orderDetailsDTO.getOrderDetailId() );
        orderDetails.setOrderId( orderDetailsDTO.getOrderId() );
        orderDetails.setChapterId( orderDetailsDTO.getChapterId() );
        orderDetails.setPrice( orderDetailsDTO.getPrice() );

        return orderDetails;
    }

    @Override
    public void updateOrderDetails(OrderDetails orderDetails, OrderDetailsDTO orderDetailsDTO) {
        if ( orderDetailsDTO == null ) {
            return;
        }

        orderDetails.setOrderDetailId( orderDetailsDTO.getOrderDetailId() );
        orderDetails.setOrderId( orderDetailsDTO.getOrderId() );
        orderDetails.setChapterId( orderDetailsDTO.getChapterId() );
        orderDetails.setPrice( orderDetailsDTO.getPrice() );
    }

    @Override
    public Comics toComics(ComicsDTO comicsDTO) {
        if ( comicsDTO == null ) {
            return null;
        }

        Comics comics = new Comics();

        comics.setComicId( comicsDTO.getComicId() );
        comics.setComicName( comicsDTO.getComicName() );
        comics.setUserId( comicsDTO.getUserId() );
        comics.setCreatedDate( xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( comicsDTO.getCreatedDate() ) ) );
        comics.setQuantityChap( comicsDTO.getQuantityChap() );
        comics.setCoverUrl( comicsDTO.getCoverUrl() );
        comics.setDescription( comicsDTO.getDescription() );
        comics.setStatus( comicsDTO.getStatus() );

        return comics;
    }

    @Override
    public void updateComics(Comics comics, ComicsDTO comicsDTO) {
        if ( comicsDTO == null ) {
            return;
        }

        comics.setComicId( comicsDTO.getComicId() );
        comics.setComicName( comicsDTO.getComicName() );
        comics.setUserId( comicsDTO.getUserId() );
        comics.setCreatedDate( xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( comicsDTO.getCreatedDate() ) ) );
        comics.setQuantityChap( comicsDTO.getQuantityChap() );
        comics.setCoverUrl( comicsDTO.getCoverUrl() );
        comics.setDescription( comicsDTO.getDescription() );
        comics.setStatus( comicsDTO.getStatus() );
    }

    @Override
    public void updateOrders(Orders orders, OrdersDTO ordersDTO) {
        if ( ordersDTO == null ) {
            return;
        }

        orders.setOrderId( ordersDTO.getOrderId() );
        orders.setOrderTime( xmlGregorianCalendarToLocalDateTime( localDateToXmlGregorianCalendar( ordersDTO.getOrderTime() ) ) );
        orders.setQuantity( ordersDTO.getQuantity() );
        orders.setType( ordersDTO.getType() );
        orders.setUserId( ordersDTO.getUserId() );
    }

    @Override
    public void updateWallets(Wallets wallets, WalletsDTO owalletsDTO) {
        if ( owalletsDTO == null ) {
            return;
        }

        wallets.setWalletId( owalletsDTO.getWalletId() );
        wallets.setBalance( owalletsDTO.getBalance() );
        wallets.setUserId( owalletsDTO.getUserId() );
    }

    @Override
    public Genres toGenres(GenresDTO genresDTO) {
        if ( genresDTO == null ) {
            return null;
        }

        Genres genres = new Genres();

        genres.setGenresId( genresDTO.getGenresId() );
        genres.setGenresName( genresDTO.getGenresName() );
        genres.setGenresDescription( genresDTO.getGenresDescription() );
        genres.setStatus( genresDTO.getStatus() );

        return genres;
    }

    @Override
    public void updateGenres(Genres genres, GenresDTO genresDTO) {
        if ( genresDTO == null ) {
            return;
        }

        genres.setGenresId( genresDTO.getGenresId() );
        genres.setGenresName( genresDTO.getGenresName() );
        genres.setGenresDescription( genresDTO.getGenresDescription() );
        genres.setStatus( genresDTO.getStatus() );
    }

    @Override
    public void updatePurchasedCoins(PurchasedCoins purchasedCoins, PurchasedCoinsDTO purchasedCoinsDTO) {
        if ( purchasedCoinsDTO == null ) {
            return;
        }

        purchasedCoins.setPurchasedCoinId( purchasedCoinsDTO.getPurchasedCoinId() );
        if ( purchasedCoinsDTO.getAmount() != null ) {
            purchasedCoins.setAmount( purchasedCoinsDTO.getAmount().doubleValue() );
        }
        if ( purchasedCoinsDTO.getNumberOfCoin() != null ) {
            purchasedCoins.setNumberOfCoin( purchasedCoinsDTO.getNumberOfCoin().doubleValue() );
        }
        purchasedCoins.setType( mapType( purchasedCoinsDTO.getType() ) );
        purchasedCoins.setStatus( mapStatus( purchasedCoinsDTO.getStatus() ) );
        purchasedCoins.setDescription( purchasedCoinsDTO.getDescription() );
        purchasedCoins.setPurchaseTime( purchasedCoinsDTO.getPurchaseTime() );
        purchasedCoins.setTransactionCode( purchasedCoinsDTO.getTransactionCode() );
        purchasedCoins.setUserId( purchasedCoinsDTO.getUserId() );
        purchasedCoins.setUser( purchasedCoinsDTO.getUser() );
        if ( purchasedCoins.getTransactions() != null ) {
            List<Transactions> list = purchasedCoinsDTO.getTransactions();
            if ( list != null ) {
                purchasedCoins.getTransactions().clear();
                purchasedCoins.getTransactions().addAll( list );
            }
            else {
                purchasedCoins.setTransactions( null );
            }
        }
        else {
            List<Transactions> list = purchasedCoinsDTO.getTransactions();
            if ( list != null ) {
                purchasedCoins.setTransactions( new ArrayList<Transactions>( list ) );
            }
        }
    }

    private XMLGregorianCalendar localDateToXmlGregorianCalendar( LocalDate localDate ) {
        if ( localDate == null ) {
            return null;
        }

        return datatypeFactory.newXMLGregorianCalendarDate(
            localDate.getYear(),
            localDate.getMonthValue(),
            localDate.getDayOfMonth(),
            DatatypeConstants.FIELD_UNDEFINED );
    }

    private static LocalDateTime xmlGregorianCalendarToLocalDateTime( XMLGregorianCalendar xcal ) {
        if ( xcal == null ) {
            return null;
        }

        if ( xcal.getYear() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getMonth() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getDay() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getHour() != DatatypeConstants.FIELD_UNDEFINED
            && xcal.getMinute() != DatatypeConstants.FIELD_UNDEFINED
        ) {
            if ( xcal.getSecond() != DatatypeConstants.FIELD_UNDEFINED
                && xcal.getMillisecond() != DatatypeConstants.FIELD_UNDEFINED ) {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute(),
                    xcal.getSecond(),
                    Duration.ofMillis( xcal.getMillisecond() ).getNano()
                );
            }
            else if ( xcal.getSecond() != DatatypeConstants.FIELD_UNDEFINED ) {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute(),
                    xcal.getSecond()
                );
            }
            else {
                return LocalDateTime.of(
                    xcal.getYear(),
                    xcal.getMonth(),
                    xcal.getDay(),
                    xcal.getHour(),
                    xcal.getMinute()
                );
            }
        }
        return null;
    }
}
