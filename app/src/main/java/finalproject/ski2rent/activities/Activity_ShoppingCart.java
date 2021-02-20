package finalproject.ski2rent.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.ArrayList;

import finalproject.ski2rent.R;
import finalproject.ski2rent.adapters.Adapter_ShoppingCart;
import finalproject.ski2rent.callbacks.CallBack_UpdateOrderData;
import finalproject.ski2rent.callbacks.CallBack_UpdateShoppingCartData;
import finalproject.ski2rent.objects.Order;
import finalproject.ski2rent.objects.RentedBoard;
import finalproject.ski2rent.objects.ShoppingCart;
import finalproject.ski2rent.utils.FireBaseManager;
import finalproject.ski2rent.utils.MySignals;

public class Activity_ShoppingCart extends Activity_Base {
    public static final String EXTRA_KEY_SHOPPING_CART = "EXTRA_KEY_SHOPPING_CART";

    private ShoppingCart shoppingCart = new ShoppingCart();
    private ArrayList<RentedBoard> boardsInCart = new ArrayList<>();

    private RecyclerView shoppingCart_LST_records;
    private TextView shoppingCart_LBL_totalPrice;
    private MaterialButton shoppingCart_BTN_checkOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ShoppingCartLifeCycle", "onCreate: Activity_ShoppingCart");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__shopping_cart);

        String shoppingCartJson = getIntent().getStringExtra(EXTRA_KEY_SHOPPING_CART);
        shoppingCart = new Gson().fromJson(shoppingCartJson, ShoppingCart.class);

        findViews();

        boardsInCart = shoppingCart.getBoardsInCart();

        if (boardsInCart.size() == 0) {
            shoppingCart_BTN_checkOut.setEnabled(false);
        }

        setTextTotalPrice();

        shoppingCart_LST_records.setLayoutManager(new LinearLayoutManager(this));
        Adapter_ShoppingCart adapter_boards = new Adapter_ShoppingCart(this, boardsInCart);

        adapter_boards.setClickListener(new Adapter_ShoppingCart.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {}

            @Override
            public void onCancelItemClick(int position) {
                if (boardsInCart.size() == 0) {
                    shoppingCart_BTN_checkOut.setEnabled(false);
                    shoppingCart.setPickupDate(0);
                    shoppingCart.setReturnDate(0);
                }
                FireBaseManager fireBaseManager = FireBaseManager.getInstance();
                fireBaseManager.updateShoppingCartToServer(shoppingCart, new CallBack_UpdateShoppingCartData() {
                    @Override
                    public void updated() {}
                });
                setTextTotalPrice();
            }
        });

        shoppingCart_LST_records.setAdapter(adapter_boards);

        shoppingCart_BTN_checkOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseManager fireBaseManager = FireBaseManager.getInstance();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String uid = firebaseUser.getUid();
                ArrayList<RentedBoard> boardsToOrder = adapter_boards.getRentedBoards();
                Order order = new Order(uid, boardsToOrder, shoppingCart.getPickupDate(), shoppingCart.getReturnDate());

                fireBaseManager.updateOrderToServer(uid, order,  new CallBack_UpdateOrderData() {
                    @Override
                    public void updated() {
                        MySignals.getInstance().toast("Thanks for buying!");
                        finish();
                        // return
                    }
                });
            }
        });

    }

    private void setTextTotalPrice() {
        shoppingCart_LBL_totalPrice.setText("Total price: € " + shoppingCart.calculateTotalPrice());
    }

    private void findViews() {
        shoppingCart_LST_records = findViewById(R.id.shoppingCart_LST_records);
        shoppingCart_LBL_totalPrice = findViewById(R.id.shoppingCart_LBL_totalPrice);
        shoppingCart_BTN_checkOut = findViewById(R.id.shoppingCart_BTN_checkOut);
    }

    @Override
    protected void onStart() {
        Log.d("ShoppingCartLifeCycle", "onStart: Activity_ShoppingCart");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d("ShoppingCartLifeCycle", "onResume: Activity_ShoppingCart");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("ShoppingCartLifeCycle", "onPause: Activity_ShoppingCart");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("ShoppingCartLifeCycle", "onStop: Activity_ShoppingCart");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("ShoppingCartLifeCycle", "onDestroy: Activity_ShoppingCart");
        super.onDestroy();
    }


} // Activity_ShoppingCart