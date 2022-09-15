const Sequelize = require('sequelize');

module.exports = class Board extends Sequelize.Model{
    static init(sequelize){
        return super.init({

            available:{
                type:Sequelize.STRING(10),
                allowNull:false,
            },

            postID:{
                type:Sequelize.STRING(10),
                allowNull:false,
            },

            userEmail:{
                type:Sequelize.STRING(100),
                allowNull: false,
            },
            postTitle:{
                type:Sequelize.STRING(50),
                allowNull: false,
            },
            postContent:{
                type:Sequelize.STRING(1000),
                allowNull: false,
            },
            postStartLatitude:{
                type:Sequelize.STRING(100),
                allowNull:false,
            },
            postStartLongitude:{
                type:Sequelize.STRING(100),
                allowNull:false,
            },
            postEndLatitude:{
                type:Sequelize.STRING(100),
                allowNull:false,
            },
            postEndLongitude:{
                type:Sequelize.STRING(100),
                allowNull:false,
            },
            //postLocation:{
              //  type:Sequelize.STRING(30),
                //allowNull: false,
//            },
        },
        {
            sequelize,
            timestamps:false,
            underscored:false,
            modelName:'Board',
            tableName:'board',
            paranoid:false,
            charset:'utf8',
            collate:'utf8_general_ci',
            freezeTableName:true,
        }
        );
    }
}