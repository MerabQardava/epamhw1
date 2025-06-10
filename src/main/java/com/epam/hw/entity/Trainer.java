package com.epam.hw.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Trainer{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="specializationId")
    public TrainingType specializationId;

    @OneToOne
    @JoinColumn(name="user_id", nullable=false, unique = true)
    public User user;

    public Trainer(TrainingType specializationId, User user) {
        this.specializationId = specializationId;
        this.user = user;
    }




    @Override
    public String toString() {
        return "Trainer{" +
                "specializationId=" + specializationId +
                ", userId=" + user.getUserId()+
                ", firstName='" + user.getFirstName() + '\'' +
                ", lastName='" + user.getLastName() + '\'' +
                ", username='" + user.getUsername() + '\'' +
                ", password='" + user.getPassword() + '\'' +
                ", isActive=" + user.isActive() +
                '}';
    }

}
