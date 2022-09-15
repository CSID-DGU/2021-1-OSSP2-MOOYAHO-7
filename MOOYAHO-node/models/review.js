const Sequelize = require('sequelize');

module.exports = class Review extends Sequelize.Model{
    static init(sequelize){
        return super.init({

            reviewSender:{
                type:Sequelize.STRING(50),
                allowNull:false,
            },
            
            reviewReceiver:{
                type:Sequelize.STRING(50),
                allowNull:false,
            },

            reviewContent:{
                type:Sequelize.STRING(200),
                allowNull:false,
            },

            reviewDate:{
                type:Sequelize.STRING(30),
                allowNull:false,

            },
            
            reviewRate:{
                type:Sequelize.STRING(3),
                allowNull:false,
            },
        },
        {
            sequelize,
            timestamps:false,
            underscored:false,
            modelName:'Review',
            tableName:'review',
            paranoid:false,
            charset:'utf8',
            collate:'utf8_general_ci',
            freezeTableName:true,
        }
        );
    }
}