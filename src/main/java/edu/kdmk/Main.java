package edu.kdmk;

import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.ValidationAction;
import com.mongodb.client.model.ValidationOptions;
import edu.kdmk.config.MongoConfig;
import edu.kdmk.managers.ClientManager;
import edu.kdmk.managers.GameManager;
import edu.kdmk.managers.RentManager;
import edu.kdmk.models.Client;
import edu.kdmk.models.game.BoardGame;
import edu.kdmk.models.game.ComputerGame;
import edu.kdmk.models.game.Game;
import edu.kdmk.repositories.GameRepository;
import org.bson.Document;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {

    }
}