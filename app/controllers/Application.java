package controllers;

import play.*;
import play.data.FormFactory;
import play.mvc.*;
import play.db.jpa.*;
import views.html.*;
import models.Person;
import play.data.Form;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

import static play.libs.Json.*;

@Singleton
public class Application extends Controller {

    private final JPAApi jpaApi;

    private final FormFactory formFactory;

    @Inject
    public Application(JPAApi pJpaApi, FormFactory pFormFactory) {

        this.jpaApi = pJpaApi;
        this.formFactory = pFormFactory;
    }

    public Result index() {
        return ok(index.render());
    }

    @Transactional
    public Result addPerson() {
        Person person = formFactory.form(Person.class).bindFromRequest().get();

        jpaApi.withTransaction("defaultx", false, () -> {

            jpaApi.em().persist(person);

            return person;
        });

        return redirect(routes.Application.index());
    }

    @Transactional(readOnly = true)
    public Result getPersons() {

        List<Person> persons = jpaApi.withTransaction("default", true, () ->
                (List<Person>) JPA.em().createQuery("select p from Person p").getResultList());

        return ok(toJson(persons));
    }
}
